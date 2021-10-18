<p:library xmlns:p="http://www.w3.org/ns/xproc"
           xmlns:cx="http://xmlcalabash.com/ns/extensions"
           xmlns:xs="http://www.w3.org/2001/XMLSchema"
           version="3.0">

<!-- I'm setting use-when=false here because technically you aren't
     allowed to automatically declare extension steps. -->
<p:declare-step type="cx:template-java" use-when="false()">
  <p:input port="source" content-types="any" sequence="true"/>
  <p:output port="result" content-types="xml"/>
  <p:option name="option" as="xs:boolean" select="false()"/>
</p:declare-step>

</p:library>
