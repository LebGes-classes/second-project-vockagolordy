public interface Storage {
    String getLocation();
    void setLocation(String location);
    String[] getAvailableCategories();
    void newAvailableCategory(String category); //add
    void newUnavailableCategory(String category); //remove
}
