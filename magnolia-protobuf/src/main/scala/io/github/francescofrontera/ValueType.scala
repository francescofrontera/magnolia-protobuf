package io.github.francescofrontera

object ValueType {
  sealed trait VType {
    def label: String
  }

  case object ProtobufRecord extends VType {
    override def label: String = "ProtobufSchema"
  }

  case object StringType extends VType {
    override def label: String = "string"
  }

  case object IntType extends VType {
    override def label: String = "int32"
  }
}
