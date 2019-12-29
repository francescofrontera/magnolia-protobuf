package io.github.francescofrontera

private[francescofrontera] object Label {
  sealed trait Arity {
    def label: String
  }

  case object Required extends Arity {
    override val label: String = "required"
  }

  case object Optional extends Arity {
    override val label: String = "optional"
  }
}
