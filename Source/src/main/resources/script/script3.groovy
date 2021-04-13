import com.sap.gateway.ip.core.customdev.util.Message
import java.util.HashMap
import groovy.xml.*
def Message processData(Message message) {
    
    def map = message.getProperties()
    def ex = map.get("CamelExceptionCaught")
    
    if (ex!=null) {
        //Detect exception type
        def exType = ex.getClass().getCanonicalName()
        if (exType == "org.apache.camel.component.ahc.AhcOperationFailedException"){
            def responseBody = ex.getResponseBody()
            def errorMsg = ""
            //If error XML get error message, otherwise throw raw response
            try {
                def xdoc = new XmlSlurper().parseText(responseBody)
                def msg = xdoc.message.text()
                errorMsg = msg.length() > 0 ? msg : responseBody
            } catch (Exception exInner){
                //No parseable error xml found, thus throw complete response
                errorMsg = responseBody
            }
            throw new Exception(errorMsg)
        }
    }
    return message
}