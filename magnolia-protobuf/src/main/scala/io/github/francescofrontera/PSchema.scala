package io.github.francescofrontera

import com.github.os72.protobuf.dynamic.{ DynamicSchema, MessageDefinition }
import com.google.protobuf.DynamicMessage
import magnolia._

import scala.language.experimental.macros

sealed trait FieldDesc extends Serializable {
  def required: Label.Arity = Label.Required
  def valueType: ValueType.VType
}

sealed trait PSchema[T] extends FieldDesc {
  type Out
  type From

  def to(in: T): Out
  def from(in: From): T
  def cast(in: Any): T = in.asInstanceOf[T]
}

object PSchema {
  sealed trait Type[T] extends PSchema[T] {
    type Out  = DynamicMessage
    type From = DynamicMessage

    def schema: DynamicSchema
  }

  sealed trait Identity[IN0] extends PSchema[IN0] {
    type Out  = IN0
    type From = IN0

    override def to(in: IN0): IN0   = in
    override def from(in: IN0): IN0 = in
  }

  type Typeclass[T] = PSchema[T]

  //Primitives
  implicit val stringType: PSchema[String] = new Identity[String] {
    def valueType: ValueType.VType = ValueType.StringType
  }

  implicit val intType: PSchema[Int] = new Identity[Int] {
    def valueType: ValueType.VType = ValueType.IntType
  }

  //TODO: Enrich with all primitives
  implicit def optType[T](implicit t: PSchema[T]): PSchema[Option[T]] =
    new PSchema[Option[T]] {
      override type Out  = T
      override type From = T

      override def required: Label.Arity = Label.Optional

      def valueType: ValueType.VType = t.valueType

      def from(in: T): Option[T] = Option(in)

      def to(in: Option[T]): T = in.getOrElse(null.asInstanceOf[T])
    }

  // Derive with magnolia..
  def combine[T](caseClass: CaseClass[Typeclass, T]): Type[T] =
    new Type[T] {

      def from(in: DynamicMessage): T =
        caseClass.construct { params =>
          val f = in.getDescriptorForType.findFieldByName(params.label)
          params.typeclass.cast(in.getField(f))
        }

      def to(in: T): Out = {
        val builder    = schema.newMessageBuilder(s"${caseClass.typeName.short}Message")
        val descriptor = builder.getDescriptorForType

        caseClass.parameters
          .foldLeft(builder) {
            case (acc, field) =>
              val value = field.typeclass.to(field.dereference(in))
              if (value == null) acc
              else acc.setField(descriptor.findFieldByName(field.label), value)
          }
          .build()
      }

      def valueType: ValueType.VType = ValueType.ProtobufRecord

      override def schema: DynamicSchema = {
        val msgName = s"${caseClass.typeName.short}Message"
        val (builder, _) = caseClass.parameters
          .foldLeft((MessageDefinition.newBuilder(msgName), 0)) {
            case ((b, count), p) =>
              val inc = count + 1
              (
                b.addField(
                  p.typeclass.required.label,
                  p.typeclass.valueType.label,
                  p.label,
                  inc
                ),
                inc
              )
          }

        DynamicSchema
          .newBuilder()
          .setName(s"${caseClass.typeName.short}.proto")
          .addMessageDefinition(builder.build())
          .build()
      }
    }

  implicit def gen[T]: Type[T] = macro Magnolia.gen[T]

  def apply[T](implicit schema: PSchema.Type[T]): PSchema.Type[T] = schema
}
