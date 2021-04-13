import com.sap.gateway.ip.core.customdev.util.Message
import java.util.HashMap

def Message processData(Message message) {
    def map = message.getHeaders()
    def token = map.get("x-csrf-token").toString()
    message.setHeader("x-csrf-token", token)
    return message
}