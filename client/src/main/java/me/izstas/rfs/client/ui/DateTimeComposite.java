package me.izstas.rfs.client.ui;

import java.util.Calendar;
import java.util.Date;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

/**
 * A composite consisting of two DateTime widgets - for date and time.
 */
public final class DateTimeComposite extends Composite {
    private DateTime dateDateTime;
    private DateTime timeDateTime;

    /**
     * Constructs the composite.
     */
    public DateTimeComposite(Composite parent, int style) {
        super(parent, style);

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);

        dateDateTime = new DateTime(this, SWT.BORDER | SWT.DROP_DOWN);
        timeDateTime = new DateTime(this, SWT.BORDER | SWT.TIME);
    }

    /**
     * Returns the datetime specified by the widgets.
     */
    public Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateDateTime.getYear(), dateDateTime.getMonth(), dateDateTime.getDay(),
                timeDateTime.getHours(), timeDateTime.getMinutes(), timeDateTime.getSeconds());

        return calendar.getTime();
    }

    /**
     * Sets the widgets to represent the specified date.
     */
    public void setDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        dateDateTime.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_YEAR));
        timeDateTime.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }
}
