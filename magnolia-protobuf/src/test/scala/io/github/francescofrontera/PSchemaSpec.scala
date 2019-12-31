package io.github.francescofrontera

import com.github.os72.protobuf.dynamic.DynamicSchema
import com.google.protobuf.{ Descriptors, DynamicMessage }
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.forAll
import org.scalacheck._

object PSchemaSpec extends Properties("PSchema") {
  import PSchema._

  private[this] case class DataType(aNumber: Int, aString: String)
  case class DataTypeWithOpt(aNumber: Int, aStringOpt: Option[String])

  private[this] val randomDataType: Gen[DataType] = for {
    randomNumber <- arbitrary[Int]
    randomString <- arbitrary[String]
  } yield DataType(randomNumber, randomString)

  private[this] val randomDataTypeOpt: Gen[DataTypeWithOpt] = for {
    randomNumber <- arbitrary[Int]
    randomString <- arbitrary[Option[String]]
  } yield DataTypeWithOpt(randomNumber, randomString)

  private[this] implicit val arb: Arbitrary[DataType]           = Arbitrary(randomDataType)
  private[this] implicit val arbOpt: Arbitrary[DataTypeWithOpt] = Arbitrary(randomDataTypeOpt)

  property("to") = forAll { in: DataType =>
    val pSchema = PSchema[DataType]

    val producedDMsg: Array[Byte] = pSchema.to(in).toByteArray
    val (builder, desc)           = asDynamicMessage(pSchema.schema, classOf[DataType].getSimpleName)
    val pDynamicMessage: Array[Byte] =
      builder
        .setField(desc.findFieldByName("aNumber"), in.aNumber)
        .setField(desc.findFieldByName("aString"), in.aString)
        .build()
        .toByteArray

    producedDMsg.sameElements(pDynamicMessage)
  }

  property("to") = forAll { in: DataTypeWithOpt =>
    val pSchema = PSchema[DataTypeWithOpt]

    val producedDMsg: Array[Byte] = pSchema.to(in).toByteArray
    val (builder, desc)           = asDynamicMessage(pSchema.schema, classOf[DataTypeWithOpt].getSimpleName)

    val pDynamicMessage =
      builder
        .setField(desc.findFieldByName("aNumber"), in.aNumber)

    val optField = in.aStringOpt.fold(pDynamicMessage) { fieldV =>
      builder.setField(desc.findFieldByName("aStringOpt"), fieldV)
    }

    producedDMsg.sameElements(optField.build().toByteArray)
  }

  private def asDynamicMessage(schema: DynamicSchema,
                               msgName: String): (DynamicMessage.Builder, Descriptors.Descriptor) = {
    val schemaBuilder = schema.newMessageBuilder(s"${msgName}Message")
    val desc          = schemaBuilder.getDescriptorForType
    (schemaBuilder, desc)
  }
}
