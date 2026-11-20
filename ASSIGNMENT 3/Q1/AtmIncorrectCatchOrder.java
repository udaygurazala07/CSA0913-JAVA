package q1;

/**
 * Demonstrates what happens when a broader catch block is placed before a narrower one.
 * 
 * In Java, subclasses of Exception must be caught BEFORE superclasses.
 * Placing catch (Exception e) before catch (NullPointerException e) causes a compile-time error:
 * "unreachable catch block".
 */
public class AtmIncorrectCatchOrder {
    public static void main(String[] args) {
        System.out.println("Demonstrating catch order rule:");
        
        try {
            String str = null;
            System.out.println(str.length());
        } catch (Exception e) {
            System.out.println("Caught by generic Exception: " + e.getMessage());
        } 
        /*
        // UNCOMMENTING THE FOLLOWING WILL CAUSE COMPILER ERROR:
        catch (NullPointerException npe) {
            System.out.println("Caught specific NullPointerException");
        }
        // Compiler error:
        // error: exception NullPointerException has already been caught
        */
        
        System.out.println("Notice: If catch(Exception) is placed above catch(NullPointerException),");
        System.out.println("javac flags a compile-time error: 'exception NullPointerException has already been caught'.");
    }
}
