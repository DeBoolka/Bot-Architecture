package dikanev.nikita.core.service.server.URLParameter;

import java.util.*;

public interface Parameter{

    Parameter getParameter(String parameter);

    String getContent();
    String getContent(String param);

    /**
     *
     * @param param название параметра
     * @return {@code List<String>} со значениями входного параметра.
     *         {@code null}, если значение отсутствует
     * @throws IllegalFormatException если значение невозможно преобразовать в список строк
     */
    List<String> get(String param);

    /**
     *
     * @param param название параметра
     * @param def список со значениями по умолчанию
     * @return {@code List<String>} со значениями входного параметра.
     *         {@code def}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в список строк
     */
    List<String> getOrDefault(String param, List<String> def);

    /**
     *
     * @param param название параметра
     * @return {@code List<Integer>} со значениями входного параметра.
     *         {@code null}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в список чисел
     */
    List<Integer> getInt(String param);

    /**
     *
     * @param param название параметра
     * @param def значение по умолчанию
     * @return {@code List<Integer>} со значениями входного параметра.
     *         {@code def}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в список чисел
     */
    List<Integer> getIntOrDefault(String param, List<Integer> def);

    /**
     * Ищет первое попавшееся значение входного параметра
     *
     * @param param название параметра
     * @return значение входного параметра.
     *         {@code null}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в строку
     */
    String getF(String param);

    /**
     * Ищет первое попавшееся значение входного параметра.
     *
     * @param param название параметра
     * @param def значение по умолчанию
     * @return значение входного параметра.
     *         {@code def}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в строку
     */
    String getFOrDefault(String param, String def);

    /**
     * Ищет первое попавшееся значение входного параметра.
     *
     * @param param название параметра
     * @return значение входного параметра.
     *         {@code null}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в число
     */
    int getIntF(String param);

    /**
     * Ищет первое попавшееся значение входного параметра.
     *
     * @param param название параметра
     * @param def значение по умолчанию
     * @return значение входного параметра.
     *         {@code def}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в число
     */
    int getIntFOrDefault(String param, int def);

    /**
     *
     * @param param название параметра
     * @return {@code true}, если есть такой параметр
     */
    boolean contains(String param);

    /**
     *
     * @param params название параметров
     * @return {@code true}, если есть параметры
     */
    boolean containsAll(List<String> params);

    /**
     * Проверяет, что все значения присутствуют в параметре
     *
     * @param param название параметра
     * @param vals проверяемы значения
     * @return {@code true}, если все значения есть в параметре
     */
    boolean containsAllVal(String param, List<String> vals);

    /**
     * Проверяет, что значение присутствует в параметре
     *
     * @param param название параметра
     * @param val проверяемое значение
     * @return {@code true}, если значения есть в параметре
     */
    boolean containsVal(String param, String val);

    boolean isEmpty();
}
