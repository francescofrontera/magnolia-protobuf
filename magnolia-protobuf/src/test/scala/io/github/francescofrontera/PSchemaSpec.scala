package io.github.francescofrontera

import com.github.os72.protobuf.dynamic.DynamicSchema
import com.google.protobuf.DynamicMessage
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.forAll
import org.scalacheck._

object PSchemaSpec extends Properties("PSchema") {
  import scala.jdk.CollectionConverters._
  import PSchema._

  case class DataType(aNumber: Int, aString: String)

  val randomDataType: Gen[DataType] = for {
    randomNumber <- arbitrary[Int]
    randomString <- arbitrary[String]
  } yield DataType(randomNumber, randomString)

  implicit val arb = Arbitrary(randomDataType)

  property("to") = forAll { in: DataType =>
    val pSchema = PSchema[DataType]

    val producedDMsg: Array[Byte]    = pSchema.to(in).toByteArray
    val pDynamicMessage: Array[Byte] = asDynamicMessage(in, pSchema.schema).toByteArray

    producedDMsg.sameElements(pDynamicMessage)
  }

  private def asDynamicMessage(in: DataType, schema: DynamicSchema): DynamicMessage = {
    val schemaBuilder = schema.newMessageBuilder("DataTypeMessage")
    val desc          = schemaBuilder.getDescriptorForType

    val msg = schemaBuilder
      .setField(desc.findFieldByName("aNumber"), in.aNumber)
      .setField(desc.findFieldByName("aString"), in.aString)
      .build()

    msg
  }
}
