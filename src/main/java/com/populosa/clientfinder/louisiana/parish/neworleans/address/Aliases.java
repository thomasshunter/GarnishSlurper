package com.populosa.clientfinder.louisiana.parish.neworleans.address;

import java.io.Serializable;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class Aliases implements Serializable
{
    private String originalWholeName;
    private String[] aliases;
    private String[] firstAndLastOfOneAlias;
    
    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        
        out.append( "\n-------------------------" );
        out.append( "\n        Aliases          " );
        out.append( "\n-------------------------" );
        out.append( "\n originalWholeName      =" + this.originalWholeName );
        out.append( "\n-------------------------" );
        out.append( "\n aliases:                " + "\n" + java.util.Arrays.toString( aliases ) );
        out.append( "\n-------------------------" );
        out.append( "\n firstAndLastOfOneAlias: " + "\n" + java.util.Arrays.toString( firstAndLastOfOneAlias ) );
        out.append( "\n-------------------------" );
        
        return out.toString();
    }
   
    
    public Aliases( String originalWholeNm )
    {
        this.originalWholeName  = originalWholeNm;
        String wholeName        = originalWholeNm.replaceAll( ", ET AL", "" );     
        this.aliases            = wholeName.split( "A/K/A" );

        if( aliases.length > 1 )
        {
            Aliases.LOG.info( "Encountered 'A/K/A' name with wholeName=" + wholeName );
            firstAndLastOfOneAlias = aliases[0].split( " " );  // Constructor defaults to the first alias.
        }
        else
        {
            firstAndLastOfOneAlias = wholeName.split( " " );
        }

        if( firstAndLastOfOneAlias.length == 3 )
        {
            String middle = firstAndLastOfOneAlias[ 1 ];
            
            if( 
                    middle != null 
                    && 
                    (
                            middle.trim().equalsIgnoreCase( "von" )
                            ||
                            middle.trim().equalsIgnoreCase( "der" )
                    )
              )
            {
                String last = middle + " " + firstAndLastOfOneAlias[ 2 ];
                firstAndLastOfOneAlias[ 2 ]  = last;
            }
            
            System.out.println( "middle=" + middle );
        }        
    }
    
    public String[] breakIntoFirstAndLast( int indexOfAlias )
    {   
        if( this.aliases.length > indexOfAlias )
        {
            String wholeName                = this.aliases[ indexOfAlias ];
            String[] firstAndLastOfOneAlias = wholeName.trim().split( " " );
            
            if( firstAndLastOfOneAlias.length == 3 )
            {
                String middle       = firstAndLastOfOneAlias[ 1 ];
                int indexOfPeriod   = middle.indexOf( "." );
                
                if( 
                        middle != null 
                        && 
                        (
                                middle.trim().equalsIgnoreCase( "von" )
                                ||
                                middle.trim().equalsIgnoreCase( "der" )
                        )
                  )
                {
                    String last = middle + " " + firstAndLastOfOneAlias[ 2 ];
                    firstAndLastOfOneAlias[ 2 ]  = last;
                }
                else if( middle != null && indexOfPeriod > -1 )
                {
                    String first = firstAndLastOfOneAlias[0] + " " + firstAndLastOfOneAlias[1];
                    firstAndLastOfOneAlias[0] = first;
                    firstAndLastOfOneAlias[1] = firstAndLastOfOneAlias[2];
                }
                else if( middle != null )
                {
                    String first = firstAndLastOfOneAlias[0] + " " + firstAndLastOfOneAlias[1];
                    firstAndLastOfOneAlias[0] = first;
                    firstAndLastOfOneAlias[1] = firstAndLastOfOneAlias[2];                    
                }
            }
            
            return firstAndLastOfOneAlias;
        }
        
        return null;
    }
    
    private static Logger LOG = Logger.getLogger( Aliases.class );

    public String[] findFirstAndLastNames()
    {
        return firstAndLastOfOneAlias;
    }

    public String getOriginalWholeName()
    {
        return originalWholeName;
    }

    public String[] getAliases()
    {
        return aliases;
    }

    public String[] getFirstAndLastOfOneAlias()
    {
        return firstAndLastOfOneAlias;
    }
    
}
