package com.shark.somnia.client.gui.widget;

import net.minecraft.client.gui.widget.ButtonWidget;

public class OptionToggleWidget extends ButtonWidget {
    private boolean checked;
    private String baseText;

    public OptionToggleWidget(int id, int x, int y, String text, boolean checked) {
        this(id, x, y, 200, 20, text, checked);
    }

    public OptionToggleWidget(int id, int x, int y, int width, int height, String text, boolean checked) {
        super(id, x, y, width, height, text);
        this.baseText = text;
        setChecked(checked);
    }

    public void toggle() {
        setChecked(!isChecked());
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    private void resetText() {
        text = baseText + ": " + checkedString();
    }

    private String checkedString() {
        return checked ? "Yes" : "No";
    }
}
