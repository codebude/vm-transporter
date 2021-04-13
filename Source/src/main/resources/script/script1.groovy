import com.sap.gateway.ip.core.customdev.util.Message
import java.util.HashMap
import java.util.zip.*

def Message processData(Message message) {
    
    //Prepare general information
    byte[] body = message.getBody(byte[].class)
    def artifactName = "PR004_VM001_OrgId_to_Different"
    
    //Prepare zip stream
    ByteArrayOutputStream baos = new ByteArrayOutputStream()
    ZipOutputStream zipFile = new ZipOutputStream(baos)  
 
    //Add value mapping XML to zip
    addEntry(zipFile, body, "value_mapping.xml")
    
    //Add metainfo.prop to zip
    addEntry(zipFile, """#Store metainfo properties
#${new Date().format("E MMM dd HH:mm:ss z yyyy")}
description=This value mapping was automatically imported from SAP PO at ${new Date().format("yyyy-MM-dd HH:mm:ss.S")}
source=
target=""".bytes, "metainfo.prop")
    
    //Add .project to zip
    addEntry(zipFile, """<?xml version="1.0" encoding="UTF-8"?><projectDescription>
   <name>${artifactName}</name>
   <comment/>
   <projects/>
   <buildSpec>
      <buildCommand>
         <name>org.eclipse.jdt.core.javabuilder</name>
         <arguments/>
      </buildCommand>
   </buildSpec>
   <natures>
      <nature>org.eclipse.jdt.core.javanature</nature>
      <nature>com.sap.ide.ifl.project.support.project.nature</nature>
      <nature>com.sap.ide.ifl.valmap</nature>
   </natures>
</projectDescription>""".bytes, ".project")
    
    
    //Add MANIFEST.MF to zip
    zipFile.putNextEntry(new ZipEntry("META-INF/"));
    addEntry(zipFile, """Manifest-Version: 1.0
Bundle-SymbolicName: ${artifactName}; singleton:=true
Bundle-Name: ${artifactName}
Bundle-Version: 1.0.0
Bundle-ManifestVersion: 2
Bundle-Activator: com.sap.xi.mapping.camel.valmap.ValueMappingActivato
 r
SAP-ArtifactTrait: 
SAP-NodeType: IFLMAP
Import-Package: com.sap.xi.mapping.camel.valmap
SAP-BundleType: ValueMapping
Import-Service: 
""".bytes, "META-INF/MANIFEST.MF")
    
    
    //Finish zip file
    zipFile.close()  

    def zipAsBase64 = baos.toByteArray().encodeBase64().toString().replaceAll("\r?\n","")
    message.setProperty("zipData", zipAsBase64)
    message.setBody(zipAsBase64)
    return message;
}

def addEntry(def zipFile, def bytes, def name){
    zipFile.putNextEntry(new ZipEntry(name))
    zipFile.write(bytes)
    zipFile.closeEntry()
}