## 项目介绍

项目中经常会用到将数据导出到本地，除了以日志文件、数据库的方式导出外，今天再加一种以Excel的格式导出到本地以及发送到指定邮箱的功能，数据库的内容普通用户看不懂，但是我们将数据导出的Excel中，并结合标题，可以提高用户的可读性，也可以根据内容分析终端使用过程中，遇到的各种问题等等，为了大家方便集成以及统一管理，我将这一部分内容封装成了一个maven仓库，大家只要在项目中添加依赖就可以直接使用了，如果在使用过程中发现bug可以向我反馈，我会尽快修改
   
GitHub地址：https://github.com/zhuyuhao121/ExportDataToExcel
   
## 依赖添加
在你的项目根目录下的build.gradle文件中加入依赖
``` java
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
添加依赖
``` java
dependencies {
    implementation 'com.github.zhuyuhao121:ExportDataToExcel:1.0.0'
}
```

## 参数定义
``` java
private String[] topic = {"序号", "测试员Id", "部件名称", "Mac地址", "箱号", "测试点", "操作时间"
            , "回复时间", "回复数据", "是否成功"};

private static final String pathName = "zhilai_ceshi"; //存储路径名称
private static final String fileName = "record"; //文件名称，由于中文名称会乱码，所以最好不要命名为中文
private static final String content = "请输入邮件内容"; //邮件内容
private static final String subject = "请输入邮件主题"; //邮件主题
private static final String bottomName = "Excel底部名称"; //Excel底部名称
private static final String emailAccount = "xxx@chinawebox.com"; //发件人邮箱账号（最好使用公司账号）
private static final String emailPwd = "xxxxxx"; //发件人邮箱密码
private static final String mailHost = "smtp.ym.163.com"; //发件服务器地址

``` 

## 初始化
``` java
/**
 * 初始化
 *
 * @param isSendEmail 是否发送邮件，true 发送邮件， false 不发送，只把数据保存在本地
 */
private void init(boolean isSendEmail) {
    ExportBll.getInstance().init(MainActivity.this
            , fileName + DataUtils.DateStr(), content, subject
            , bottomName, topic, emailAccount, emailPwd, mailHost);
    if (isSendEmail) {
        ExportBll.getInstance()
                .showDialogView(true)//是否显示自带的dialog
                .excelExportData(true, getBaseLists());
    } else {
        ExportBll.getInstance()
                .excelExportData(false, getBaseLists());
    }
}

```

## 获取数据库中的数据

for循环里的添加顺序要跟参数定义中的标题栏顺序一致，否则数据将会错位
``` java
/**
 * 将需要导出的数据按照以下格式转换成字符串
 *
 * @return
 */
private List<BaseBean> getBaseLists() {
    List<BaseBean> baseBeans = new ArrayList<>();
    for (int i = 0; i < allLists.size(); i++) {
        OpenDoorRecordBean bean = allLists.get(i);
        BaseBean baseBean = new BaseBean();
        List<String> strings = new ArrayList<>();
        strings.add(bean.getId() + "");
        strings.add(bean.getTesterId());
        strings.add(bean.getPartName());
        strings.add(bean.getBleAddress());
        strings.add(bean.getBoxId() + "");
        strings.add(bean.getBehavior());
        strings.add(bean.getOperationTime());
        strings.add(bean.getReplyTime());
        strings.add(bean.getReplyData());
        strings.add(bean.getResultMsg());
        baseBean.setList(strings);
        baseBeans.add(baseBean);
    }
    return baseBeans;
}
