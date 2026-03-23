package modele;

/**
 * Représente un livre de la médiathèque.
 */
public class Livre extends DocumentAbstrait {
    private final int nombrePages;

    public Livre(String id, String titre, int nombrePages) {
        super(id, titre);
        this.nombrePages = nombrePages;
    }

    public int getNombrePages() {
        return nombrePages;
    }

    @Override
    public String toString() {
        return "Livre " + super.toString() + " - " + nombrePages + " pages";
    }
}
