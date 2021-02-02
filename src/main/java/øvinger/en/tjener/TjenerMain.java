package øvinger.en.tjener;

public class TjenerMain {


    public static void main(String[] args) {
        try {
            Tjener tj = new Tjener(1250);
        } catch (Exception e) {
            System.out.println(e.toString());
        }


    }
}
