SurePass2_News
==============

“逢考资讯” 教育新闻资讯客户端  

该app已经发布至各大应用市场    http://www.mumayi.com/android-339029.html

  我是一名android开发者，我受惠于互联网，受惠于开源，感谢开源时代授予我知识和技能。知恩必定图报，现将本人的
一个个人作品贡献出来，欢迎各位大大、牛牛过来吐槽。
  此作品分为客户端+服务器端，完全由个人独力完成，现仅共享客户端代码，现客户端还不是十分完善，希望各位大大帮
与完善。

客户端设计逻辑：
  这是一个新闻资讯的阅读客户端，新闻内容为获取特定页面的特定内容，用jsoup解析特定的网站。
  
  解析网址对照如下表：(后台支持分页)
  焦点：http://edu.163.com/special/00293HDB/toutiaolist.html
  公务员: http://www.chinagwy.org/html/xwsz/1_1.html
  就业: http://job.jyb.cn/jysx/index.html
  高考: http://gaokao.jyb.cn/gksx/index.html
  考研: http://kaoyan.jyb.cn/kysx/index.html
  国内: http://china.jyb.cn/yaowen/index.html
  国际: http://world.jyb.cn/gjsx/index.html
  
  代码CommentSetting.java类中：
  public final static String HOST = "1.surepass2server.sinaapp.com";
  public final static String STORAGR = "surepass2server-surepass2domain.stor.sinaapp.com";
  这两句请不必修改，因需要连接后台服务器。
  
现希望完善的功能：
  现在在查看新闻正文内容的时候，有部分内容解析得不是很完美。特别是“焦点”类别中的正文解析，如果内容有图片轮
换、分页等复杂排版时，解析得不是很好，希望各位大大将这一块修整修整，在下不胜感激！！！！

