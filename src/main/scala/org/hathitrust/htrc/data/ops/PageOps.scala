package org.hathitrust.htrc.data.ops

import org.hathitrust.htrc.data.ops.TextOptions._
import org.hathitrust.htrc.textprocessing.runningheaders.{Lines, Page}
import org.hathitrust.htrc.tools.scala.implicits.CollectionsImplicits._

object PageOps {
  protected val EndParagraphPunct: Set[Char] = Set('.', '?', '!')
}

trait PageOps { self: Page =>
  import PageOps._

  def text(textOptions: TextOptions*): String = applyTextOptions(textLines, textOptions)

  protected def applyTextOptions(textLines: Lines, textOptions: Seq[TextOptions]): String =
    textOptions match {
      case Nil => text
      case _ =>
        @SuppressWarnings(Array("org.wartremover.warts.Var"))
        var lines = textLines.iterator

        if (textOptions contains TrimLines)
          lines = lines.map(_.trim)

        if (textOptions contains RemoveEmptyLines)
          lines = lines.filter(_.nonEmpty)

        if (textOptions contains DehyphenateAtEol)
          lines = lines.dehyphenate()

        if (textOptions contains ParaLines)
          lines = lines.filter(_.nonEmpty)
            .groupConsecutiveWhen((l1, _) => !EndParagraphPunct.contains(l1.last))
            .map(_.reduceOption(_ + " " + _) match {
              case Some(s) => s
              case None => ""
            })

        lines.mkString("", System.lineSeparator(), System.lineSeparator())
    }
}
