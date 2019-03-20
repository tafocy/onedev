package io.onedev.server.web.editable.enumeration;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.ConversionException;

import io.onedev.server.web.component.stringchoice.StringSingleChoice;
import io.onedev.server.web.editable.ErrorContext;
import io.onedev.server.web.editable.PathElement;
import io.onedev.server.web.editable.PropertyDescriptor;
import io.onedev.server.web.editable.PropertyEditor;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class EnumPropertyEditor extends PropertyEditor<Enum<?>> {

	private final Class<Enum> enumClass;
	
	private StringSingleChoice input;
	
	public EnumPropertyEditor(String id, PropertyDescriptor propertyDescriptor, IModel<Enum<?>> propertyModel) {
		super(id, propertyDescriptor, propertyModel);
		
		enumClass = (Class<Enum>) propertyDescriptor.getPropertyGetter().getReturnType();		
	}

	private String getDisplayValue(String choice) {
		return StringUtils.capitalize(choice.replace('_', ' ').toLowerCase());
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();

		Map<String, String> choices = new LinkedHashMap<>();
        for (Iterator<?> it = EnumSet.allOf(enumClass).iterator(); it.hasNext();) {
            Enum<?> value = (Enum<?>) it.next();
            choices.put(value.name(), getDisplayValue(value.toString()));
        }

        String selection;
        if (getModelObject() != null)
        	selection = getModelObject().name();
        else
        	selection = null;
        
		input = new StringSingleChoice("input", Model.of(selection), choices) {

			@Override
			protected void onInitialize() {
				super.onInitialize();
				getSettings().configurePlaceholder(descriptor, this);
			}
			
		};
        // add this to control allowClear flag of select2
    	input.setRequired(descriptor.isPropertyRequired());
        input.setLabel(Model.of(getDescriptor().getDisplayName(this)));
        
		input.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				onPropertyUpdating(target);
			}
			
		});
        
        add(input);
    }

	@Override
	public ErrorContext getErrorContext(PathElement element) {
		return null;
	}

	@Override
	protected Enum<?> convertInputToValue() throws ConversionException {
		String convertedInput = input.getConvertedInput();
		if (convertedInput != null) 
			return Enum.valueOf(enumClass, convertedInput);
		else
			return null;
	}

}
