To RUN:

     1) COPY AND PASTE THE FOLDER: "Stock_Analyzer" into any directory.
    
     2) To compile the Program go inside the Directory : "Stock_Analyzer"
        and run:  
              ant
 
     3) To run the Program use a terminal with the directory pointing to : "Calendar-Dev-v0.7"
        and run: //-Xmx64M
             java -jar script/stockretrieval.jar ../data/company.stock
             java -cp .:lib/commons-lang3-3.1.jar:lib/mysql-connector-java-5.0.8-bin.jar:poi-3.8-20120326.jar stockretrieval.mainApp ../data/company.stock
             

To know the commands type: ant -projecthelp

ant : To Compile the Code
ant run : To Run the Code
ant jar : Creates a executable jar excluding the source file and build.xml file
ant runJ : Runs the executable jar, it creates the jar by default
