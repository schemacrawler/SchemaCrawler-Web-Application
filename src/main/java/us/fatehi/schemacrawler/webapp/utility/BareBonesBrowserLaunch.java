package us.fatehi.schemacrawler.webapp.utility;


import java.awt.Desktop;
import java.net.URI;
import java.util.Arrays;

/**
 * <b>Bare Bones Browser Launch for Java</b><br>
 * Utility class to open a web page from a Swing application in the
 * user's default browser.<br>
 * Supports: Mac OS X, GNU/Linux, Unix, Windows XP/Vista/7<br>
 * Example Usage:<code><br> &nbsp; &nbsp;
 *    String url = "http://www.google.com/";<br> &nbsp; &nbsp;
 *    BareBonesBrowserLaunch.openURL(url);<br></code> Latest Version:
 * <a href=
 * "http://centerkey.com/java/browser/">centerkey.com/java/browser</a><br>
 * Author: Dem Pilafian<br>
 * Public Domain Software -- Free to Use as You Like
 * 
 * @version 3.2, October 24, 2010
 */
public class BareBonesBrowserLaunch
{

  static final String[] browsers = {
                                     "x-www-browser",
                                     "google-chrome",
                                     "firefox",
                                     "opera",
                                     "epiphany",
                                     "konqueror",
                                     "conkeror",
                                     "midori",
                                     "kazehakase",
                                     "mozilla" };
  static final String errMsg = "Error attempting to launch web browser";

  /**
   * Opens the specified web page in the user's default browser
   * 
   * @param url
   *        A web address (URL) of a web page (ex:
   *        "http://www.google.com/")
   */
  public static void openURL(String url)
  {
    try
    {
      final Desktop desktop = Desktop.getDesktop();
      final URI webappUrl = URI.create(url);
      desktop.browse(webappUrl);
    }
    catch (Exception ignore)
    {
      // library not available or failed
      String osName = System.getProperty("os.name");
      try
      {
        if (osName.startsWith("Mac OS"))
        {
          Class.forName("com.apple.eio.FileManager")
            .getDeclaredMethod("openURL", new Class[] { String.class })
            .invoke(null, new Object[] {
                                         url });
        }
        else if (osName.startsWith("Windows"))
          Runtime.getRuntime()
            .exec("rundll32 url.dll,FileProtocolHandler " + url);
        else
        {
          // assume Unix or Linux
          String browser = null;
          for (String b: browsers)
            if (browser == null && Runtime.getRuntime().exec(new String[] {
                                                                            "which",
                                                                            b })
              .getInputStream().read() != -1)
              Runtime.getRuntime().exec(new String[] {
                                                       browser = b,
                                                       url });
          if (browser == null) throw new Exception(Arrays.toString(browsers));
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }

}
