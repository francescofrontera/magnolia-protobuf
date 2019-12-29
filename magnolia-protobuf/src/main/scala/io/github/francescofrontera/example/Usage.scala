package io.github.francescofrontera.example

import com.github.os72.protobuf.dynamic.DynamicSchema
import com.google.protobuf.DynamicMessage
import io.github.francescofrontera.PSchema

object Usage extends App {
  import PSchema._

  case class Person(name: String, age: Int)

  val result: Type[Person] = PSchema[Person]

  val schema: DynamicSchema        = result.schema
  val covertToDMsg: DynamicMessage = result.to(Person("Franco", 28))

  val from: Person = result.from(covertToDMsg)

  println("#########")
  println(s"Schema: $schema")

  println("#########")
  println(s"Serialization Size: ${covertToDMsg.getSerializedSize}, ${covertToDMsg.getAllFields}")

  println("#########")
  println(from)
}
