package com.dhkun.test.mybatis.plugin;

import java.util.Arrays;
import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import com.dhkun.test.mybatis.constants.DefaultField;

public class UpdateBatchByPrimaryKeySelectiveElementGenerator extends
		AbstractXmlElementGenerator {

	/**
     * 更新操作忽略'created'字段
     */
    private static final List<DefaultField> UPDATEBATCHBYPRIMARYKEYSELECTIVE_FIELDS = Arrays.asList(DefaultField.DISABLED_INGORED, DefaultField.CREATED_INGORED, DefaultField.UPDATED);
	
	public UpdateBatchByPrimaryKeySelectiveElementGenerator() {
		super();
	}
	
	@Override
	public void addElements(XmlElement parentElement) {
		XmlElement answer = new XmlElement("update");
		answer.addAttribute(new Attribute("id", "updateByPrimaryKeySelective")); //$NON-NLS-1$
		answer.addAttribute(new Attribute("parameterType", "java.util.List"));
		
		XmlElement forEachElement = new XmlElement("foreach");
        forEachElement.addAttribute(new Attribute("collection", "list"));
        forEachElement.addAttribute(new Attribute("item", "item"));
        forEachElement.addAttribute(new Attribute("index", "index"));
        forEachElement.addAttribute(new Attribute("separator", ";"));
        answer.addElement(forEachElement);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("update "); //$NON-NLS-1$
		sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
		forEachElement.addElement(new TextElement(sb.toString()));
		
		XmlElement dynamicElement = new XmlElement("set"); //$NON-NLS-1$
		forEachElement.addElement(dynamicElement);
		
		for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
			String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
			if (containsDefaultField(columnName, UPDATEBATCHBYPRIMARYKEYSELECTIVE_FIELDS)) {
				addUpdateDefaultField(columnName, dynamicElement, UPDATEBATCHBYPRIMARYKEYSELECTIVE_FIELDS);
			} else {
			    XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
			    sb.setLength(0);
			    sb.append(introspectedColumn.getJavaProperty());
			    sb.append(" != null"); //$NON-NLS-1$
			    isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
			    dynamicElement.addElement(isNotNullElement);
			
			    sb.setLength(0);
			    sb.append(columnName);
			    sb.append(" = "); //$NON-NLS-1$
			    sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
			    sb.append(',');
			
			    isNotNullElement.addElement(new TextElement(sb.toString()));
			}
		}
		
		boolean and = false;
		for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
		    sb.setLength(0);
		    if (and) {
		        sb.append("  and "); //$NON-NLS-1$
		    } else {
		        sb.append("where "); //$NON-NLS-1$
		        and = true;
		    }
		
		    sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
		    sb.append(" = "); //$NON-NLS-1$
		    sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
		    forEachElement.addElement(new TextElement(sb.toString()));
		}
		System.out.println("新输出的updateBatchSelectiveByPrimaryKey语句为：\n" + answer.getFormattedContent(0));
        
        parentElement.addElement(answer); // 添加到父节点中
	}
	
	private boolean containsDefaultField(String columnName, List<DefaultField> defaultFields) {
    	for (DefaultField defaultField : defaultFields) {
    		if (defaultField.getField().equals(columnName)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private void addUpdateDefaultField(String columnName, XmlElement dynamicElement, List<DefaultField> defaultFields) {
    	StringBuilder sb = new StringBuilder();
    	for (DefaultField defaultField : defaultFields) {
    		if (defaultField.getField().equals(columnName)
    				&& defaultField.getDefaultValue() != null) {
	    		sb.setLength(0);
			    sb.append(columnName);
			    sb.append(" = "); //$NON-NLS-1$
			    sb.append(defaultField.getDefaultValue());
			    sb.append(',');
			    dynamicElement.addElement(new TextElement(sb.toString()));
    		}
		}
    }

}
