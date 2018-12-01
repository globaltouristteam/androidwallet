package io.goshisoft.marketcap;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

public class CustomRadioGroup extends AppCompatRadioButton {

    private Listener listener;

    public CustomRadioGroup(Context context) {
        super(context);
    }

    public CustomRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRadioGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void toggle() {
        if (listener != null && isChecked()) {
            listener.toggle();
        }
        super.toggle();
    }

    interface Listener {
        void toggle();
    }
}
