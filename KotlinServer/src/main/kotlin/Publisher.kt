import com.rabbitmq.client.ConnectionFactory
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*

const val EXCHANGE_NAME = "pub-sub-exchange"

fun main(args: Array<String>) {
    val factory = ConnectionFactory()
    factory.newConnection("amqp://guest:guest@localhost:5672/").use { connection ->
        connection.createChannel().use { channel ->

            channel.exchangeDeclare(EXCHANGE_NAME, "fanout")

            println("Publishing messages to exchange: ${EXCHANGE_NAME}")

            repeat(50) {
                val message = """
                    {
                        "somekey": "someval",
                        "someotherkey": 1234,
                        "iteration": ${it},
                        "timeEpochSecond": "${Instant.now().epochSecond}"
                    }
                """.trimIndent()

                channel.basicPublish(
                    EXCHANGE_NAME,
                    "",
                    null,
                    message.toByteArray(StandardCharsets.UTF_8)
                )
                println(" [x] Sent '$message'")

                Thread.sleep(50)
            }
        }
    }
}