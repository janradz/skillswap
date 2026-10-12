package de.fhmuenster.skillswap.exception;

import java.time.LocalDate;


public class HolidayException extends RuntimeException
{
    private final LocalDate date;
    private final String holidayName;

    public HolidayException(LocalDate date, String holidayName)
    {
        super(String.format("Termin kann nicht am %s gebucht werden (%s)", date, holidayName));
        this.date = date;
        this.holidayName = holidayName;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public String getHolidayName()
    {
        return holidayName;
    }
}