package Project1.model.daoImpl;

import Project1.bizImpl.GetTime;
import Project1.model.*;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by snwfnh on 2016/8/25.
 */
public class IUserDaoImpl {
    Connection mConnection;
    PreparedStatement mPreparedStatement;
    ResultSet mResultSet;
    List<BookInfo> bookInfoList;
    List<User> userList;
    List<LendRecord> lendRecordList;
    List<Book> bookList;
    List<EvaluateRecord> evaluateRecordList;
    List<OrdBook> ordBookList;
    List<FrozeRecord> frozeRecordList;
    Book mbook;
    BookInfo mBookInfo;
    int resultCode;
    User mUser;
    EvaluateRecord mEvaluateRecord;
    LendRecord mLendRecord;
    OrdBook mOrdBook;
    FrozeRecord mFrozeRecord;
    Scanner mScanner;
    int biid;
    int bid;
    String sql;
    String title;
    String title_id;

    public IUserDaoImpl() {
        mConnection = DBHelp.getInstance().getConnection();
        mbook = new Book();
        mBookInfo = new BookInfo();
        mUser = new User();
        mEvaluateRecord = new EvaluateRecord();
        mLendRecord = new LendRecord();
        mOrdBook = new OrdBook();
        mFrozeRecord = new FrozeRecord();
        frozeRecordList = new ArrayList<>();
        bookList = new ArrayList<>();
        bookInfoList = new ArrayList<>();
        userList = new ArrayList<>();
        evaluateRecordList = new ArrayList<>();
        lendRecordList = new ArrayList<>();
        ordBookList = new ArrayList<>();
        mScanner = new Scanner(System.in);

    }

    public boolean checkFrozen(int uid) {
        sql = "select id, uid, frozenTime, unfrozenTime from frozenrecord where uid=?";
        try {
            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setInt(1, uid);
            mResultSet = mPreparedStatement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (mResultSet.next())
                resultCode = 1;
            else
                resultCode = 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultCode > 0;
    }

    public void lendBook(int uid) throws SQLException {
        if (!checkFrozen(uid)) {
            System.out.println("请输入要借的图书书本id");
            biid = mScanner.nextInt();
            mBookInfo.setId(biid);
            title = "bookinfo";
            title_id = "id";
            if (checkid(biid, title_id, title) && checkbiid(biid)) {
                sql = "insert into lendrecord (uid,biid,bid,lendTime,returnTime) values (?,?,?,?,?)";

                mPreparedStatement = mConnection.prepareStatement(sql);
                mPreparedStatement.setInt(1, uid);
                mPreparedStatement.setInt(2, mBookInfo.getId());
                mPreparedStatement.setInt(3, mBookInfo.getBid());
                mPreparedStatement.setString(4, GetTime.getInstance().getCurrTime());
                mPreparedStatement.setString(5, GetTime.getInstance().getAfterTime());
                resultCode = mPreparedStatement.executeUpdate();
            }
            if (resultCode > 0)
                System.out.println("借书成功");
            else
                System.out.println("借书失败");

        } else
            System.out.println("帐号被冻结，不能借书");


    }


    public void returnBook() {
        System.out.println("请输入所还书书本的id号：");
        biid = mScanner.nextInt();
        title = "lendrecord";
        title_id = "biid";
        try {
            if (checkid(biid, title_id, title)) {
                sql = "update lendrecord set returnTime=? where biid=?";
                mPreparedStatement = mConnection.prepareStatement(sql);
                mPreparedStatement.setString(1, GetTime.getInstance().getCurrTime());
                mPreparedStatement.setInt(2, biid);
                resultCode = mPreparedStatement.executeUpdate();
                if (resultCode > 0)
                    System.out.println("还书成功");
            } else
                System.out.println("还书失败");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void ordBook(int uid) throws SQLException {
        System.out.println("请输入要预约的图书表id");
        bid = mScanner.nextInt();
        title = "book";
        title_id = "id";
        if (checkid(bid, title_id, title)) {
            sql = "insert into ordbook (uid,bid,ordTime) values (?,?,?)";
            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setInt(1, uid);
            mPreparedStatement.setInt(2, bid);
            mPreparedStatement.setString(3, GetTime.getInstance().getCurrTime());
            resultCode = mPreparedStatement.executeUpdate();
            if (resultCode > 0)
                System.out.println("预约成功");

        } else
            System.out.println("预约失败，图书馆没有此书 ");


    }


    public void evaluate(int uid) throws SQLException {
        System.out.println("请输入要评价的图书书表id");
        bid = mScanner.nextInt();
        title = "book";
        title_id = "id";
        if (checkid(bid, title_id, title)) {

            sql = "insert into evaluaterecord (uid,bid,content,conTime) values (?,?,?,?)";
            System.out.println("请输入评价的内容");
            String content;
            content = mScanner.next();


            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setInt(1, uid);
            mPreparedStatement.setInt(2, bid);
            mPreparedStatement.setString(3, content);
            mPreparedStatement.setString(4, GetTime.getInstance().getCurrTime());
            resultCode = mPreparedStatement.executeUpdate();

            if (resultCode > 0)
                System.out.println("评价成功");
            ;
        } else
            System.out.println("查不到你所输入的图书书表信息");
    }

    public boolean checkbiid(int biid) {
        sql = "select * from bookinfo where id=?";

        try {
            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setInt(1, biid);
            mResultSet = mPreparedStatement.executeQuery();
            if (mResultSet.next()) {
                mBookInfo = new BookInfo(mResultSet.getInt("id"), mResultSet.getInt("bid"),
                        mResultSet.getInt("inout"), mResultSet.getInt("state"), mResultSet.getInt("lost"));

            }
            if (mBookInfo.getInout() == 0)
                resultCode = 0;
            else if (mBookInfo.getInout() == 1)
                resultCode = 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultCode > 0;
    }

    public boolean checkid(int bid, String str1, String str2) throws SQLException {
        sql = "select id from book where id=?";
        sql = sql.replace("id", str1);
        sql = sql.replace("book", str2);
        mPreparedStatement = mConnection.prepareStatement(sql);
        mPreparedStatement.setInt(1, bid);
        mResultSet = mPreparedStatement.executeQuery();
        if (mResultSet.next())
            resultCode = 1;
        else
            resultCode = 0;
        return resultCode > 0;
    }

    public void showInfo(int useId) {
        System.out.println("请输入指令查看或修改个人信息");
        System.out.println("*1* 查看个人信息");
        System.out.println("*2* 查看个人借还书记录");
        System.out.println("*3* 查看书本评价信息");
        System.out.println("*4* 修改登录密码");
        System.out.println("*5* 返回用户主界面");
        int choose = mScanner.nextInt();
        switch (choose) {
            case 1:
                sql = "select id,name,level from user where id=?";
                sql = sql.replace("?", "" + useId);
                showUser(sql);
                showInfo(useId);
                break;
            case 2:
                sql = "select id, uid, biid,bid, lendTime, returnTime from lendrecord where uid=?";
                sql = sql.replace("?", "" + useId);
                showLendRecord(sql);
                showInfo(useId);
                break;
            case 3:
                sql = "select id, uid, bid, content, conTime from evaluaterecord";
                showEvaluate(sql);
                showInfo(useId);
                break;
            case 4:
                updatePwd(useId);
                showInfo(useId);
                break;
            case 5:
                break;
            default:
                System.out.println("输入指令错误，请重新输入");
                showInfo(useId);
                break;
        }


    }


    public void showUser(String sql) {
        try {
            mPreparedStatement = mConnection.prepareStatement(sql);
            mResultSet = mPreparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (mResultSet == null)
            System.out.println("没有记录");
        try {
            while (mResultSet.next()) {

               List<User> userList=new ArrayList<>();
                mUser.setId(mResultSet.getInt("id"));
                mUser.setName(mResultSet.getString("name"));
                mUser.setLevel(mResultSet.getInt("level"));
                userList.add(mUser);
                for (User user : userList)
                    System.out.println(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public void showLendRecord(String sql) {
        try {
            mPreparedStatement = mConnection.prepareStatement(sql);
            mResultSet = mPreparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (mResultSet == null)
            System.out.println("没有记录");
        try {
            while (mResultSet.next()) {
                List<LendRecord> lendRecordList=new ArrayList<>();
                mLendRecord.setId(mResultSet.getInt("id"));
                mLendRecord.setUid(mResultSet.getInt("uid"));
                mLendRecord.setBiid(mResultSet.getInt("biid"));
                mLendRecord.setBiid(mResultSet.getInt("bid"));
                mLendRecord.setLendTime(mResultSet.getString("lendTime"));
                mLendRecord.setReturnTime(mResultSet.getString("returnTime"));
                lendRecordList.add(mLendRecord);
                for (LendRecord lendRecord : lendRecordList)
                    System.out.println(lendRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void showEvaluate(String sql) {
        try {
            mPreparedStatement = mConnection.prepareStatement(sql);
            mResultSet = mPreparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (mResultSet == null)
            System.out.println("没有此记录");
        try {
            while (mResultSet.next()) {
                List<EvaluateRecord> evaluateRecordList=new ArrayList<>();

                mEvaluateRecord.setId(mResultSet.getInt("id"));
                mEvaluateRecord.setUid(mResultSet.getInt("uid"));
                mEvaluateRecord.setBid(mResultSet.getInt("bid"));
                mEvaluateRecord.setContent(mResultSet.getString("content"));
                mEvaluateRecord.setConTime(mResultSet.getString("conTime"));
                evaluateRecordList.add( mEvaluateRecord);
                for (EvaluateRecord evaluateRecord : evaluateRecordList)
                    System.out.println(evaluateRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void updatePwd(int useId) {
        System.out.println("请输入新密码");
        String st1 = mScanner.next();
        String st2 = mScanner.next();
        if (!st1.equals(st2)) {
            System.out.println("两次输入的密码不相同，请重新输入");
            updatePwd(useId);
        } else {
            sql = "update user set password=? where id=?";

            try {
                mPreparedStatement = mConnection.prepareStatement(sql);
                mPreparedStatement.setString(1, st1);
                mPreparedStatement.setInt(2, useId);
                resultCode = mPreparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if (resultCode > 0)
            System.out.println("密码修改成功");
        else
            System.out.println("密码修改失败");

    }
}
