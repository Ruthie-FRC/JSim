package jsim.field;

/**
 * Single JSON per season config parser mapping.
 */
public class FieldConfig {
    public String season;
    public FieldElement[] fieldElements;

    public FieldConfig() {
        this.fieldElements = new FieldElement[0];
    }
}
