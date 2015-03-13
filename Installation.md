#How to install this program and run it

# Installation #

## Required Software ##

#. Install MySQL5.6 >
#. Install Eclipse Luna

## Download sources and make schema ##

#. Download this sources by using svn client. (such like tortoise svn)
#. In eclipse, use import project in the package explorer.
#. Make 'stock' database in mysql. ( account & password of 'stock' db is root/aaaa1111)
#. Each 'Dao' class sources has DDL statment for stock tables which should exist in 'stock' database.
#. Use DDL to make table in 'stock' database

## Initialize company list ##

#. Set focus to 'src/robot/company/CompanyListUpdatorFromKrx.java' in package explorer.
#. Click right mouse button, and select 'run as java application'.
#: The company list extracted from KRX will be inserted into 'tb\_company\_and\_deffered' table.
#. Run 'src/robot/financialReport/FinancialReportListUpdatorFromFnguide.java'
#: The financial reports from KRX will be inserted into 'tb\_company\_stat'




# Details #

Add your content here.  Format your content with:
  * Text in **bold** or _italic_
  * Headings, paragraphs, and lists
  * Automatic links to other wiki pages