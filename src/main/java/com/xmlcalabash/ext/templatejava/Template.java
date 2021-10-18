package com.xmlcalabash.ext.templatejava;

import com.xmlcalabash.model.util.SaxonTreeBuilder;
import com.xmlcalabash.model.util.XProcConstants;
import com.xmlcalabash.runtime.BinaryNode;
import com.xmlcalabash.runtime.StaticContext;
import com.xmlcalabash.runtime.XProcMetadata;
import com.xmlcalabash.runtime.XmlPortSpecification;
import com.xmlcalabash.steps.DefaultXmlStep;
import com.xmlcalabash.util.MediaType;
import com.xmlcalabash.util.TypeUtils;
import net.sf.saxon.om.AttributeMap;
import net.sf.saxon.om.EmptyAttributeMap;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmArray;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmMap;
import net.sf.saxon.s9api.XdmNode;

public class Template extends DefaultXmlStep {
    private int binary = 0;
    private int byteCount = 0;
    private int markup = 0;
    private int json = 0;
    private int text = 0;
    private int lineCount = 0;
    private int unknown = 0;

    @Override
    public XmlPortSpecification inputSpec() {
        return XmlPortSpecification.ANYSOURCESEQ();
    }

    @Override
    public XmlPortSpecification outputSpec() {
        return XmlPortSpecification.XMLRESULT();
    }

    @Override
    public void receive(String port, Object item, XProcMetadata metadata) {
        if (item instanceof BinaryNode) {
            binary += 1;
            byteCount += ((BinaryNode) item).bytes().length;
        } else if (item instanceof XdmNode) {
            if (metadata.contentType().textContentType()) {
                text += 1;
                lineCount += ((XdmNode) item).getStringValue().split("\\n").length;
            } else {
                markup += 1;
            }
        } else if (item instanceof XdmMap || item instanceof XdmArray || item instanceof XdmAtomicValue) {
            json += 1;
        } else {
            // ???
            unknown += 1;
        }
    }

    @Override
    public void run(StaticContext context) {
        super.run(context);

        SaxonTreeBuilder builder = new SaxonTreeBuilder(config());
        builder.startDocument(context.baseURI());
        builder.addStartElement(XProcConstants.c_result());

        AttributeMap amap = EmptyAttributeMap.getInstance();
        if (byteCount > 0) {
            amap = amap.put(TypeUtils.attributeInfo(new QName("", "bytes"), "" + byteCount));
        }
        builder.addStartElement(new QName("", "binary"), amap);
        builder.addText("" + binary);
        builder.addEndElement();

        builder.addStartElement(new QName("", "markup"));
        builder.addText("" + markup);
        builder.addEndElement();

        amap = EmptyAttributeMap.getInstance();
        if (lineCount > 0) {
            amap = amap.put(TypeUtils.attributeInfo(new QName("", "lines"), "" + lineCount));
        }
        builder.addStartElement(new QName("", "text"), amap);
        builder.addText("" + text);
        builder.addEndElement();

        builder.addStartElement(new QName("", "json"));
        builder.addText("" + json);
        builder.addEndElement();

        builder.addEndElement();
        builder.endDocument();
        consumer().receive("result", builder.result(), new XProcMetadata(MediaType.XML()));
    }
}
