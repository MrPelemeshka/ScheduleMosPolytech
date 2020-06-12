package com.example.mospolytech;

        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.widget.ArrayAdapter;
        import java.text.DateFormat;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.Locale;

public class DataAdapter extends ArrayAdapter<String> {


    SQLiteDatabase mDb;

    // Конструктор
    public DataAdapter(Context context, int textViewResourceId, SQLiteDatabase mdb) {
        super(context, textViewResourceId);
        // TODO Auto-generated constructor stub
        this.mDb = mdb;
    }

    public void refill(int GroupID, int DayID, int mode) {
        this.clear();
        String schedule;
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeText = timeFormat.format(d);
        Cursor cursor1 = null;
        if (mode == 1) {
            cursor1 = mDb.rawQuery("Select Pairs.[Begin], Pairs.[End], Subjects.Name, Schedule._id, strftime('%s',Pairs.[Begin]) From Schedule" +
                            " INNER JOIN Subjects on Schedule.SubjectID = Subjects._id INNER JOIN Pairs on Schedule.Regnr = Pairs.Regnr and Schedule.Evening = Pairs.Evening WHERE Schedule.GroupID = ? AND" +
                            " Schedule.DayID = ? AND " +
                            "((strftime('%s',time('now','localtime'))-strftime('%s',Pairs.[Begin])) between 0 and 5400)" +
                            " union " +
                            "Select Pairs.[Begin], Pairs.[End], Subjects.Name, Schedule._id, strftime('%s',Pairs.[Begin]) From Schedule" +
                            " INNER JOIN Subjects on Schedule.SubjectID =  Subjects._id INNER JOIN Pairs on Schedule.Regnr = Pairs.Regnr and Schedule.Evening = Pairs.Evening WHERE Schedule.GroupID = ? AND" +
                            " Schedule.DayID = ? AND (strftime('%s',time('now','localtime'))<strftime('%s',Pairs.[Begin])) " +
                            "order by strftime('%s',Pairs.[Begin]) asc" +
                            " limit 2 ",
                    new String[]{String.valueOf(GroupID), String.valueOf(dayOfWeek), String.valueOf(GroupID), String.valueOf(dayOfWeek)});
        }
        if (mode == 2) {
            cursor1 = mDb.rawQuery("Select Pairs.[Begin], Pairs.[End], Subjects.Name, Schedule._id, strftime('%s',Pairs.[Begin]) From Schedule" +
                            " INNER JOIN Subjects on Schedule.SubjectID =  Subjects._id INNER JOIN Pairs on Schedule.Regnr = Pairs.Regnr and Schedule.Evening = Pairs.Evening WHERE Schedule.GroupID = ? AND Schedule.DayID = ? order by strftime('%s',Pairs.[Begin]) asc",
                    new String[]{String.valueOf(GroupID), String.valueOf(DayID)});
        }
        assert cursor1 != null;
        cursor1.moveToFirst();
        schedule = "";
        while (!cursor1.isAfterLast()) {
            this.add(cursor1.getString(0));
            Cursor cursor3 = mDb.rawQuery("select Rooms.Name from Schedule join" +
                            " Rooms on Schedule.RoomID = Rooms._id where" +
                            " Schedule._id = ? ",
                    new String[]{cursor1.getString(3)});
            cursor3.moveToFirst();
            while (!cursor3.isAfterLast()) {
                schedule += cursor3.getString(0) + ", ";
                cursor3.moveToNext();
            }
            if (schedule.substring(schedule.length() - 2).equals(", ")) {
                schedule = schedule.substring(0, schedule.length() - 2);
            }
            this.add(schedule);
            schedule = "";
            this.add(cursor1.getString(1));

            Cursor cursor2 = mDb.rawQuery("select Teachers.Name from Schedule join" +
                            " Teachers on Schedule.TeacherID = Teachers._id where" +
                            " Schedule._id = ?",
                    new String[]{cursor1.getString(3)});
            cursor2.moveToFirst();
            while (!cursor2.isAfterLast()) {
                schedule += cursor2.getString(0) + ", ";
                cursor2.moveToNext();
            }
            if (schedule.substring(schedule.length() - 2).equals(", ")) {
                schedule = schedule.substring(0, schedule.length() - 2);
            }
            this.add(schedule);
            schedule = "";
            cursor1.moveToNext();
            cursor2.close();
        }
        cursor1.close();
    }
}
