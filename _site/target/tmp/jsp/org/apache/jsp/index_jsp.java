package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, false, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\r\n");
      out.write("<!DOCTYPE html>\r\n");
      out.write("<html lang=\"en\">\r\n");
      out.write("<title>Book Recommender</title>\r\n");
      out.write("<head>\r\n");
      out.write("<meta charset=\"utf-8\">\r\n");
      out.write("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n");
      out.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n");
      out.write("<title>Recommender</title>\r\n");
      out.write("\r\n");
      out.write("<!-- Bootstrap -->\r\n");
      out.write("<link href=\"lib/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\">\r\n");
      out.write("\r\n");
      out.write("<!--  Effects -->\r\n");
      out.write("<link href=\"lib/css/recommenderGUI.css\" rel=\"stylesheet\">\r\n");
      out.write("\r\n");
      out.write("<!-- Spinner doesn't seem to be working? -->\r\n");
      out.write("<link href=\"lib/css/ladda-themeless.min.css\" rel=\"stylesheet\">\r\n");
      out.write("<script src=\"lib/js/spin.min.js\"></script>\r\n");
      out.write("<script src=\"lib/js/ladda.min.js\"></script>\r\n");
      out.write("\r\n");
      out.write("</head>\r\n");
      out.write("<body class=\"center\">\r\n");
      out.write("\t<h1>BiblioPal - Book for You</h1>\r\n");
      out.write("\t<img src=\"resources/mahout-logo-poweredby-100.png\" alt=\"Mahout\">\r\n");
      out.write("\t<p></p>\r\n");
      out.write("\t<br>\r\n");
      out.write("\t<div>\r\n");
      out.write("\t\t<form method=\"GET\" action=\"servlet\" class=\"form-inline\">\r\n");
      out.write("\t\t\t<button type=\"submit\" id=\"build\" name=\"run\" value=\"Build Model\"\r\n");
      out.write("\t\t\t\tclass=\"btn btn-primary ladda-button\" data-style=\"expand-right\">\r\n");
      out.write("\t\t\t\t<span class=\"ladda-label\">Build Model</span>\r\n");
      out.write("\t\t\t</button>\r\n");
      out.write("\t\t\t<p></p>\r\n");
      out.write("\t\t\tand\r\n");
      out.write("\t\t\t<p></p>\r\n");
      out.write("\t\t\t<input type=\"text\" class=\"form-control\" placeholder=\"Input user ID\"\r\n");
      out.write("\t\t\t\tname=\"Submit\" id=\"userID\">\r\n");
      out.write("\t\t\t<button type=\"button\" id=\"recommend\" value=\"Recommend\"\r\n");
      out.write("\t\t\t\tclass=\"btn btn-primary\">Recommend</button>\r\n");
      out.write("\r\n");
      out.write("\t\t</form>\r\n");
      out.write("\t</div>\r\n");
      out.write("\t</br>\r\n");
      out.write("\t<div id=\"prompt\" class=\"active\" align=\"center\"></div>\r\n");
      out.write("\r\n");
      out.write("\t<div id=\"history\"></div>\r\n");
      out.write("\r\n");
      out.write("\t<div id=\"recommendation\"></div>\r\n");
      out.write("\r\n");
      out.write("\t<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->\r\n");
      out.write("\t<script\r\n");
      out.write("\t\tsrc=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js\"></script>\r\n");
      out.write("\t<script src=\"lib/js/recommenderGUI.js\">\r\n");
      out.write("\t\t\r\n");
      out.write("\t</script>\r\n");
      out.write("\t<!-- Include all compiled plugins (below), or include individual files as needed -->\r\n");
      out.write("\t<script src=\"lib/bootstrap/js/bootstrap.min.js\"></script>\r\n");
      out.write("</body>\r\n");
      out.write("</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
