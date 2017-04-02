package models;

/**
 * Описує об'єкти, які містять поле "id".
 *
 * Created by shrralis on 2/19/17.
 */
public interface Identifiable {
    /**
     * Повертає унікальний ідентифікатор об'єкту(взагалі, це є полем "id" з БД).
     */
    Integer getId();
}
