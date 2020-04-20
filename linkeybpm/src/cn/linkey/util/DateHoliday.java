package cn.linkey.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;

/**
 * 修改日记，增加 clearStatus()方法用来清缓存
 * 2.getDifTime()方法中改为调用DateUtil中的getAllDifTime()方法
 * 
 * @author Administrator
 *
 */
public class DateHoliday {
    public String Status;
    public String WeekDays = "";
    public String AMStartTime = "";
    public String AMEndTime = "";
    public String PMStartTime = "";
    public String PMEndTime = "";
    public String Holidays = "";
    public String OverDays = "";
    public long WorkDayMin;

    public void clearStatus() {
        Status = null;
    }

    public String getStatus() {
        return Status;
    }

    public long getWorkDayMin() {
        return WorkDayMin;
    }

    public String getDifTime(String startTime, String endTime) {
        if (Status == null)
            init();
        if (Status == "")
            return DateUtil.getAllDifTime(startTime, endTime);

        long lDifTime = 0;
        String sDifTime = "0";

        String sAMStartTime = AMStartTime;
        String sAMEndTime = AMEndTime;
        String sPMStartTime = PMStartTime;
        String sPMEndTime = PMEndTime;

        Date dts = DateUtil.str2DateTime(startTime);
        Date dte = DateUtil.str2DateTime(endTime);
        // 检测开始时间不能大于结束时间
        if (dts.after(dte)) {
            String st = startTime;
            startTime = endTime;
            endTime = st;
            dts = DateUtil.str2DateTime(startTime);
            dte = DateUtil.str2DateTime(endTime);
        }

        if (isSameDay(dts, dte)) {
            // 同一天
            if (!isHoliday(dts)) {
                String sOneDay = startTime.substring(0, 10);
                Date dt1 = DateUtil.str2DateTime(sOneDay + " " + sAMStartTime);
                Date dt2 = DateUtil.str2DateTime(sOneDay + " " + sAMEndTime);
                Date dt3 = DateUtil.str2DateTime(sOneDay + " " + sPMStartTime);
                Date dt4 = DateUtil.str2DateTime(sOneDay + " " + sPMEndTime);
                if (dte.before(dt1)) {
                    lDifTime = 0;
                }
                else if (dts.after(dt4)) {
                    lDifTime = 0;
                }
                else if (dts.after(dt2) && dte.before(dt3)) {
                    lDifTime = 0;
                }
                else {
                    if (dts.before(dt1)) {
                        dts = dt1;
                    }
                    else if (dts.after(dt2) && dts.before(dt3)) {
                        dts = dt3;
                    }
                    if (dte.after(dt4)) {
                        dte = dt4;
                    }
                    else if (dte.after(dt2) && dte.before(dt3)) {
                        dte = dt2;
                    }
                    if (!dte.after(dt2) || !dts.before(dt3)) {
                        // 同在上午或下午
                        lDifTime = getDifMinByDate(dts, dte);
                    }
                    else {
                        // 去掉中午休息时间
                        if(dte.before(dt3)) {
                            dt3 = dte;
                        }
                        lDifTime = getDifMinByDate(dts, dte) - getDifMinByDate(dt2, dt3);
                    }
                }
            }
        }
        else {
            // 非同一天
            long lWorkDayNum = 0;
            long lWorkDayMin = WorkDayMin; // getWorkDayMin()
            // 计算开始那天花费时间
            String sOneDay = startTime.substring(0, 10);
            Date dt1 = DateUtil.str2DateTime(sOneDay + " " + sAMStartTime);
            Date dt2 = DateUtil.str2DateTime(sOneDay + " " + sAMEndTime);
            Date dt3 = DateUtil.str2DateTime(sOneDay + " " + sPMStartTime);
            Date dt4 = DateUtil.str2DateTime(sOneDay + " " + sPMEndTime);
            if (!isHoliday(dts)) {
                if (dts.before(dt1)) {
                    lDifTime = lWorkDayMin;
                }
                else if (dts.before(dt2)) {
                    lDifTime = getDifMinByDate(dts, dt2) + getDifMinByDate(dt3, dt4); // 两段时间相加或总时间减中午休息时间
                }
                else if (dts.before(dt3)) {
                    lDifTime = getDifMinByDate(dt3, dt4);
                }
                else if (dts.before(dt4)) {
                    lDifTime = getDifMinByDate(dts, dt4);
                }
                else {
                    lDifTime = 0;
                }
            }
            // 计算结束那天花费时间
            sOneDay = endTime.substring(0, 10);
            dt1 = DateUtil.str2DateTime(sOneDay + " " + sAMStartTime);
            dt2 = DateUtil.str2DateTime(sOneDay + " " + sAMEndTime);
            dt3 = DateUtil.str2DateTime(sOneDay + " " + sPMStartTime);
            dt4 = DateUtil.str2DateTime(sOneDay + " " + sPMEndTime);
            if (!isHoliday(dte)) {
                if (dte.before(dt1)) {
                    lDifTime = lDifTime + 0;
                }
                else if (dte.before(dt2)) {
                    lDifTime = lDifTime + getDifMinByDate(dt1, dte);
                }
                else if (dte.before(dt3)) {
                    lDifTime = lDifTime + getDifMinByDate(dt1, dt2);
                }
                else if (dte.before(dt4)) {
                    lDifTime = lDifTime + getDifMinByDate(dt1, dt2) + getDifMinByDate(dt3, dte); // 两段时间相加或总时间减中午休息时间
                }
                else {
                    lDifTime = lDifTime + lWorkDayMin;
                }
            }
            // 获取中间相差天数 只要日期去掉时间部分
            sOneDay = startTime.substring(0, 10);
            dts = DateUtil.str2Date(sOneDay);
            sOneDay = endTime.substring(0, 10);
            dte = DateUtil.str2Date(sOneDay);
            Calendar calendarIns = Calendar.getInstance();
            calendarIns.setTime(dts);
            for (int i = 0; i < 36525; i++) {
                // 限制在100年内
                calendarIns.add(Calendar.DATE, 1);
                dts = calendarIns.getTime();
                if (dts.equals(dte) || dts.after(dte))
                    break;
                if (!isHoliday(dts))
                    lWorkDayNum = lWorkDayNum + 1;
            }
            // 计算总数
            lDifTime = lDifTime + lWorkDayMin * lWorkDayNum;
        }

        sDifTime = String.valueOf(lDifTime);
        return sDifTime;
    }

    // 判断两时间是否同一天
    public boolean isSameDay(Date day1, Date day2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ds1 = sdf.format(day1);
        String ds2 = sdf.format(day2);
        if (ds1.equals(ds2)) {
            return true;
        }
        else {
            return false;
        }
    }

    // 判断某天是否不用上班
    public boolean isHoliday(Date day) {
        boolean isHoliday = false;
        // @SuppressWarnings("deprecation")
        // String sDayWeekNum = Integer.toString(day.getDay()); //过时不推荐使用 周日是0 周六是6
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        // 周日是1 周六是7 系统设置的是0-6
        String sDayWeekNum = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK) - 1);

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        String ds = sdf.format(day);

        String sWeekDays = WeekDays;
        String sHolidays = Holidays;
        String sOverDays = OverDays;

        String[] aWeekDays = sWeekDays.split(",");
        if (Arrays.toString(aWeekDays).indexOf(sDayWeekNum) == -1) {
            // 非工作日
            isHoliday = true;
        }
        else {
            // 是否在节假日期列表
            String[] aHolidays = sHolidays.split("\r\n");
            if (Arrays.toString(aHolidays).indexOf(ds) != -1) {
                isHoliday = true;
            }
        }
        if (isHoliday == true) {
            // 是否在加班日期列表
            String[] aOverDays = sOverDays.split("\r\n");
            if (Arrays.toString(aOverDays).indexOf(ds) != -1) {
                isHoliday = false;
            }
        }
        return isHoliday;
    }

    // 获取两时间相差分钟数
    public long getDifMinByDate(Date dts, Date dte) {
        long diff = dte.getTime() - dts.getTime();
        diff = diff / (1000 * 60);
        return diff;
    }

    // 获取一天工作分钟总数
    public void setWorkDayMin() {
        if (WorkDayMin == 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date dt1 = sdf.parse(AMStartTime);
                Date dt2 = sdf.parse(AMEndTime);
                long diff = dt2.getTime() - dt1.getTime();
                dt1 = sdf.parse(PMStartTime);
                dt2 = sdf.parse(PMEndTime);
                diff = diff + (dt2.getTime() - dt1.getTime());
                diff = diff / (1000 * 60);
                WorkDayMin = diff;
            }
            catch (Exception e) {
                System.err.println(e);
                System.err.println("时间格式不正确");
            }
        }
    }

    // 获取配置
    public void init() {
        if (Status == null) {
            Document doc = Rdb.getDocumentBySql("select * from BPM_TimeConfig where Status='1'");
            if (doc == null) {
                Status = "";
            }
            else if (doc.isNull()) {
                Status = "";
            }
            else {
                Status = doc.g("Status");
                if (Status.equals("1")) {
                    WeekDays = doc.g("WeekDays");
                    AMStartTime = doc.g("AMStartTime");
                    AMEndTime = doc.g("AMEndTime");
                    PMStartTime = doc.g("PMStartTime");
                    PMEndTime = doc.g("PMEndTime");
                    Holidays = doc.g("Holidays");
                    OverDays = doc.g("OverDays");
                    setWorkDayMin();
                }
            }
        }
    }

    // 测试数据
    public void initTest() {
        Status = "1";
        WeekDays = "1,2,3,4,5";
        AMStartTime = "08:30";
        AMEndTime = "12:00";
        PMStartTime = "13:30";
        PMEndTime = "18:00";
        Holidays = "05-01\r\n10-01\r\n10-02\r\n10-03";
        OverDays = "05-04";
        setWorkDayMin();
    }

}
