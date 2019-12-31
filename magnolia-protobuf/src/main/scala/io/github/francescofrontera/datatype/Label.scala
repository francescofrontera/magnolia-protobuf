package io.github.francescofrontera.datatype

private[francescofrontera] object Label {
  sealed trait Arity {
    def label: String

    final def isNullable: Boolean =
      if (label.equalsIgnoreCase("optional")) true
      else false
  }

  case object Required extends Arity {
    override val label: String = "required"
  }

  case object Optional extends Arity {
    override val label: String = "optional"
  }
}
