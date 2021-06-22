import java.io.*;
import java.net.*;
import java.util.* ;
import java.text.* ;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//
//  Program to compare stock performance
//  Written   in  March 2000
//
public class compare_perf {
    public static java.util.List myList ;
    public static int array_count ;
    static int  MAX_ARRAY_COUNT = 3000 ;
    static Line line_array[] = new Line[MAX_ARRAY_COUNT] ;

    public static void main(String[] args) throws Exception {
    int year_1  ;
    int int_from_year ;
    int arg_count = 0 ;
    array_count = 0 ;
   
                                                                              
        if (args.length < 1) {
            System.err.println("Usage:  java compare_perf "
                               + "ticker1 ticker2 ");
            System.err.println("Example:  java compare_perf "
                               + "INTC dell jdsu");

	    System.exit(1);
	}


                SimpleDateFormat formatter =  
                                   new SimpleDateFormat("yyyy/MM/dd"); 

		Date dt = new Date(System.currentTimeMillis());
            //    System.out.println(dt );
            //    System.out.println(formatter.format(dt) );
                String yyyy_mm_dd = (formatter.format(dt) )  ;
                String curr_year = yyyy_mm_dd.substring(0,4) ;
                String curr_month = yyyy_mm_dd.substring(5,7) ;
                String curr_date = yyyy_mm_dd.substring(8,10) ;
                // Go Back 5 Years
                int_from_year =  Integer.parseInt( curr_year ) - 5 ;


       for ( arg_count = 0 ; arg_count < args.length ; arg_count++ )
       {
        String TickerSymbol = URLEncoder.encode(args[arg_count]);

        String TargetUrl =    "http://chart.yahoo.com/table.csv?s=" + TickerSymbol +
                              "&a="
                              + curr_month
                              + "&b="
                              + curr_date
                              + "&c="
                              + int_from_year  
                              + "&d="
                              + curr_month
                              + "&e="
                              + curr_date
                              + "&f="
                              + curr_year
                              + "&g=w&q=q&y=0&z="
                              +
                              TickerSymbol + "&x=.csv"
                               ;




        URL url = new URL( TargetUrl);
	URLConnection connection = url.openConnection();
    //   connection.setDoOutput(true);

    //   PrintWriter out = new PrintWriter(
    //                          connection.getOutputStream());
    //    out.println("s=" + TargetUrl ) ;
         System.out.println("TargetUrl  is:"
                                + TargetUrl);

//       out.close();

        BufferedInputStream in = (
                                new BufferedInputStream(
                                connection.getInputStream()));

        byte[] buffer = new byte[8192] ;
       StringBuffer strbuf = new StringBuffer( 8192 ) ;
       File outdir = new File( "C:\\Stocks\\Stock Data\\"  );
       if ( outdir.mkdirs() )
       {
         System.out.println("Created Directories for:"
                                + outdir);


       }
       FileOutputStream    destination =
           new FileOutputStream( "C:\\Stocks\\Stock Data\\" + TickerSymbol +"_old.csv") ;
        while ( true )
        {
          //   System.out.println(inputLine);
         int bytes_read = in.read( buffer) ;
         if ( bytes_read == -1 ) break ;
       
            destination.write(buffer , 0, bytes_read ) ;
            System.out.println("Downloaded " + bytes_read+ " bytes for ticker " +
                   TickerSymbol );
           strbuf.append( new String( buffer , 0, bytes_read ) ) ;

         }

	in.close();
        destination.close() ;

         change_string(strbuf , TickerSymbol ) ;
       }  // End of For loop

      chartit app = new chartit( line_array );

      app.addWindowListener(
         new WindowAdapter() {
            public void windowClosing( WindowEvent e )
            {
               System.exit( 0 );
            }
         }
      );

    }   // End of main

    public static void change_string ( StringBuffer strbuf,
                                 String TickerSymbol )
    {
    int pos1, pos2, pos3, pos4, pos5 ;
    int length = 0 , count = 0 ;
   
        if ( array_count >= MAX_ARRAY_COUNT )
        {
                System.out.println("MAX_ARRAY_COUNT reached "+ array_count +"\n ") ;
                return ;
        }
        // Manipulate this string
        String s1 = new String ( strbuf.toString() )  ;
        StringTokenizer tokens = new
                                 StringTokenizer( s1, "\n" ) ;
        StringBuffer s2_buf = new  StringBuffer( ) ;
        StringBuffer actual_line_buf = new StringBuffer() ;


         tokens.nextToken() ;
        while ( tokens.hasMoreTokens() )
        {
         String curr_line = new String ( tokens.nextToken() ) ;
         pos1 = curr_line.indexOf( ',', 1) + 1 ;
         pos2 = curr_line.indexOf( ',', pos1 ) +1  ;
         pos3 = curr_line.indexOf( ',', pos2 )+ 1  ;
         pos4 = curr_line.indexOf( ',', pos3 ) + 1  ;
         pos5 = curr_line.indexOf( ',', pos4 ) + 1  ;
         length = curr_line.length() ;

         // 0 to pos1 -1 because we do not want the comma
         line_array[ array_count ] = new Line(
                         TickerSymbol ,
                         curr_line.substring( 0,(pos1 -1 ) ),
                         curr_line.substring( pos1,(pos2 - 1) ),
                         curr_line.substring( pos2,(pos3-1 ) ),
                         curr_line.substring( pos3,(pos4-1)),
                         curr_line.substring( pos4,(pos5-1)),
                         curr_line.substring( pos5,length)
                            ) ;

         array_count++ ;



     actual_line_buf.append ( curr_line.substring( 0,pos1) ) ;
     actual_line_buf.append ( curr_line.substring( pos2 , pos3) ) ;
     actual_line_buf.append ( curr_line.substring( pos3 , pos4) ) ;
     actual_line_buf.append ( curr_line.substring( pos5 , length) ) ;
     actual_line_buf.append ( "\n" ) ;


     s2_buf.insert( 0 , actual_line_buf.toString() ) ;


     actual_line_buf.delete(0 , (length + 1) ) ;
     // System.out.println("actual_line "+ actual_line_buf.toString() +"\n ") ;

 
         
        }
       try
       {
     // System.out.println("s2_buf "+ s2_buf.toString() +"\n ") ;

       
       BufferedWriter    destination_1 =
           new BufferedWriter(
           new  OutputStreamWriter(
           new FileOutputStream("C:\\Stocks\\Stock Data\\" +TickerSymbol +".csv")) ) ;

         destination_1.write(s2_buf.toString() , 0, s2_buf.toString().length() ) ;
         destination_1.flush() ;
         destination_1.close() ;

        }
        catch ( IOException io )
        {
         System.out.println("Error opening output file \n ") ;
        }


    } // End change_string



} // End of class compare_perf


class Line {
   private String ticker_str ;
   private String date_str ;
   private String open_str  ;
   private String close_str  ;
   private String high_str  ;
   private String low_str  ;
   private String vol_str  ;

   public Line ( String ticker_str,
                 String date_str,
                 String open_str,
                 String high_str,
                 String low_str,
                 String close_str,
                 String vol_str
                 )
                 {
                 this.ticker_str = ticker_str ;
                 this.date_str = date_str ;
                 this.open_str = open_str ;
                 this.high_str = high_str ;
                 this.low_str = low_str ;
                 this.close_str = close_str ;
                 this.vol_str =  vol_str ;
                 }

   public String getTicker() { return ticker_str ; }
   public String getDate() { return date_str ; }
   public String getOpen() { return open_str ; }
   public String getClose() { return close_str ; }
   public String getHigh() { return high_str ; }
   public String getVol() { return vol_str ; }
}   // End of class line



class chartit extends JFrame {
   private String s = "Using drawString!";
    int length ;
    int array_index = 0 ;
    int max_rows_for_ticker= 0  ;
    int index_for_prev_ticker=0  ;
    int max_close ;
    int curr_close ;
    int x_max = 450 ;
    int x_min = 30 ;
    int y_max = 450 ;
    int y_min = 30 ;

    int x_length = x_max -  x_min ;
    int y_length = y_max -y_min  ;
    int price_array[] ;
    Line current_line ;
    Line[] temp_line_array ;
    
    int  start_index_for_x_axis = 0 ;


   public chartit(Line[] line_array)
   {

      super( "Chart" );

      length = line_array.length ;


      price_array = new int [ length ] ;
      temp_line_array = line_array ;
      while ( array_index < length )
      {
       current_line = line_array[array_index] ;
       if (current_line == null || current_line.getTicker() == null )
                break ;

       try {
          if ( current_line.getClose() != null )
              curr_close =
               (int ) Double.parseDouble( current_line.getClose() ) ;
          else
                break ;     // Break out of the loop.
           }
       catch ( NumberFormatException nfe )
          {
             System.out.println( nfe.toString() ) ;
          }
       price_array[array_index] = curr_close ;
       if ( curr_close > max_close )
       {
        max_close = curr_close ;
       }
        
       if ( ( line_array[array_index+1] == null )  ||
           ( ( line_array[array_index+1] != null ) &&
            ( current_line.getTicker()
              != line_array[array_index+1].getTicker() )) )
       {  // Next Ticker

       //System.out.println("prev_ticker :" +  current_line.getTicker() ) ;

           if ( max_rows_for_ticker < (array_index - index_for_prev_ticker) )
           {
                 max_rows_for_ticker = array_index - index_for_prev_ticker ;
                 if (  index_for_prev_ticker  != 0 )
                     start_index_for_x_axis = index_for_prev_ticker + 1   ;

           }
           index_for_prev_ticker  = array_index  ;
       }


      
      array_index++ ;
      }      // End of while



      setSize( 500, 500 );


      setLocation(  100, 100 ) ;
      show();
   }

   public void paint( Graphics g )
   {
   int count = 0 ;
   int prev_x = x_min ;
   int prev_y = y_max ;
   int curr_x =0 ;
   int curr_y = 0 ;
   int paint_array_count = 0 ;
   int done = 0 ;
   int ticker_start_index = 0 ;

      g.setColor( Color.black );
      g.drawLine( x_min, y_max, x_max , y_max );

      g.setColor( Color.black );
      g.drawLine( x_min, y_min, x_min , y_max );

      // Add Values to Y-AXIS
      curr_x = x_min ;
      for (count = ( max_close/10 ) ; count < max_close ; count= count+( max_close/ 10 )  )
      {
      curr_y = y_max - (( count * y_length ) / max_close ) ;

      g.drawString( "__" , curr_x-5 , curr_y );
      g.drawString(  String.valueOf( count )  , curr_x-20 , curr_y );

      }

      // Add Values on X-AXIS 
      curr_y = y_max ;
      paint_array_count = 0 ;
      for (count = 0 ; count < 5 ; count++ )
      {
    
      curr_x =   x_max - ((x_length / 5) * count ) ;
        paint_array_count  = start_index_for_x_axis +
                             ( count *  max_rows_for_ticker / 5 ) ;
                            
     // System.out.println("curr_x: "  + curr_x
     //                   +" curr_y:" +  curr_y ) ;

      
      g.drawString( "|" , curr_x , curr_y );
      g.drawString(  temp_line_array[paint_array_count].getDate()  , curr_x-10 , curr_y+15 );

      }

      g.setColor( Color.red );

      while ( done == 0   )
      {
      // Draw the Graph
      prev_x = x_min ;
      prev_y = y_max ;

      for (count = 0 ; count < array_index ; count++ )
      {
      curr_x = x_max - ( count * x_length / max_rows_for_ticker ) ;
      curr_y = y_max - (price_array[ count+ticker_start_index ] * y_length / max_close ) ;
    //  System.out.println("curr_x: "  + curr_x
    //                    +" curr_y:" +  curr_y ) ;

      if ( count > 0 )
      {
        g.drawLine( prev_x, prev_y , curr_x , curr_y );
        if ( count+ticker_start_index+1 == array_index )
        {
            done = 1 ;
            break ;
        }
        if (  temp_line_array[count+ticker_start_index].getTicker() !=
              temp_line_array[count+ticker_start_index+1].getTicker() )
        {  // Next Ticker
               
                ticker_start_index += count+1 ;
             //   System.out.println("Changing color "  + ticker_start_index ) ;
                g.setColor(
                         new Color ((int) (Math.random() * 256) ,
                                    (int) (Math.random() * 256) ,
                                    (int) (Math.random() * 256) 
                         )
                  );
                  break ;

        }
      }
      else if ( count == 0 )
       g.drawString( temp_line_array[count+ticker_start_index].getTicker() , curr_x-20 , curr_y-10 );

      prev_x = curr_x ;
      prev_y = curr_y ;
      }   // End For
     
      g.drawString( String.valueOf( max_close)  ,
                  x_min-5 , y_max - (max_close * y_length / max_close ) );

      } // End While


   } // End of Paint
} // End of class chartit.

