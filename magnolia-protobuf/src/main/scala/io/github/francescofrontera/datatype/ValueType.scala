package io.github.francescofrontera.datatype

object ValueType {
  sealed trait VType {
    def label: String
  }

  case object ProtobufRecord extends VType {
    override val label: String = "ProtobufSchema"
  }

  case object StringType extends VType {
    override val label: String = "string"
  }

  case object IntType extends VType {
    override val label: String = "int32"
  }
}
