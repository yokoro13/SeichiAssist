package com.github.unchama.concurrent

import cats.effect.IO
import com.github.unchama.minecraft.actions.MinecraftServerThreadShift

object Execution {
  /**
   * 与えられた`IO`をサーバーメインスレッドで実行するように予約する。
   *
   * @return
   */
  def onServerMainThread(program: IO[Any])
                        (implicit sync: MinecraftServerThreadShift[IO]): IO[Unit] = {
    val asyncProgram = for {
      _ <- sync.shift
      _ <- program
    } yield ()

    asyncProgram.runAsync {
      case Left(error) => IO {
        println("同期実行中にエラーが発生しました。")
        error.printStackTrace()
      }
      case Right(_) => IO.pure(())
    }.toIO
  }
}
