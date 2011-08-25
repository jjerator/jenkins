package hudson.model;

import hudson.Extension;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Yevgeniy_Rainik
 * Date: 8/25/11
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class RadioParameterDefinition extends SimpleParameterDefinition {
     private final List<String> choices;
    private final String defaultValue;

    @DataBoundConstructor
    public RadioParameterDefinition(String name, String choices, String description) {
        super(name, description);
        this.choices = Arrays.asList(choices.split("\\r?\\n"));
        if (choices.length()==0) {
            throw new IllegalArgumentException("No radio found");
        }
        defaultValue = null;
    }

    public RadioParameterDefinition(String name, String[] choices, String description) {
        super(name, description);
        this.choices = new ArrayList<String>(Arrays.asList(choices));
        if (this.choices.isEmpty()) {
            throw new IllegalArgumentException("No choices found");
        }
        defaultValue = null;
    }

    private RadioParameterDefinition(String name, List<String> choices, String defaultValue, String description) {
        super(name, description);
        this.choices = choices;
        this.defaultValue = defaultValue;
    }

    @Override
    public ParameterDefinition copyWithDefaultValue(ParameterValue defaultValue) {
        if (defaultValue instanceof StringParameterValue) {
            StringParameterValue value = (StringParameterValue) defaultValue;
            return new RadioParameterDefinition(getName(), choices, value.value, getDescription());
        } else {
            return this;
        }
    }

    @Exported
    public List<String> getChoices() {
        return choices;
    }

    public String getChoicesText() {
        return StringUtils.join(choices, "\n");
    }

    @Override
    public StringParameterValue getDefaultParameterValue() {
        return new StringParameterValue(getName(), defaultValue == null ? choices.get(0) : defaultValue, getDescription());
    }

    private StringParameterValue checkValue(StringParameterValue value) {
        if (!choices.contains(value.value))
            throw new IllegalArgumentException("Illegal choice: " + value.value);
        return value;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StringParameterValue value = req.bindJSON(StringParameterValue.class, jo);
        value.setDescription(getDescription());
        return checkValue(value);
    }

    public StringParameterValue createValue(String value) {
        return checkValue(new StringParameterValue(getName(), value, getDescription()));
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {
        @Override
        public String getDisplayName() {
            return "Radio parametr";
        }

        @Override
        public String getHelpFile() {
            return "/help/parameter/choice.html";
        }
    }
}
