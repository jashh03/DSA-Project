// ==================== IMPORTS ====================
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Comparator;
import java.util.Arrays;
import java.util.LinkedList;      // used only in HashTable for buckets
import java.time.LocalDateTime;
import java.util.Scanner;

// ==================== MODEL CLASSES ====================



class Currency {
    private String code;
    private String name;
    private double rateToUSD;  // e.g., 1 USD = rateToUSD of this currency

    public Currency(String code, String name, double rateToUSD) {
        this.code = code.toUpperCase();
        this.name = name;
        this.rateToUSD = rateToUSD;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public double getRateToUSD() { return rateToUSD; }
    public void setRateToUSD(double rateToUSD) { this.rateToUSD = rateToUSD; }

    @Override
    public String toString() {
        return String.format("%s (%s): %.4f", code, name, rateToUSD);
    }
}


class ConversionRecord {
    private String fromCurrency;
    private String toCurrency;
    private double amount;
    private double result;
    private LocalDateTime timestamp;

    public ConversionRecord(String from, String to, double amount, double result) {
        this.fromCurrency = from;
        this.toCurrency = to;
        this.amount = amount;
        this.result = result;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("%.2f %s = %.2f %s at %s", amount, fromCurrency, result, toCurrency, timestamp);
    }
}

// ==================== DATA STRUCTURE CLASSES ====================

/**
 * CO2: Implementation of a singly linked list ADT.
 * Demonstrates typical operations: insert, delete, traverse.
 */
class LinkedList<T> implements Iterable<T> {
    private Node head;
    private int size;

    private class Node {
        T data;
        Node next;
        Node(T data) { this.data = data; }
    }

    // CO2: Insert operation
    public void add(T item) {
        Node newNode = new Node(item);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
        }
        size++;
    }

    // CO2: Delete operation
    public boolean remove(T item) {
        if (head == null) return false;
        if (head.data.equals(item)) {
            head = head.next;
            size--;
            return true;
        }
        Node current = head;
        while (current.next != null && !current.next.data.equals(item)) {
            current = current.next;
        }
        if (current.next != null) {
            current.next = current.next.next;
            size--;
            return true;
        }
        return false;
    }

    // CO2: Traversal / random access
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node current = head;
        for (int i = 0; i < index; i++) current = current.next;
        return current.data;
    }

    public int size() { return size; }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node current = head;
            public boolean hasNext() { return current != null; }
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}

/**
 * CO3: Stack implementation using an array.
 * Used to maintain conversion history and support undo.
 */
class Stack<T> {
    private T[] arr;
    private int top;
    private static final int INITIAL_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public Stack() {
        arr = (T[]) new Object[INITIAL_CAPACITY];
        top = -1;
    }

    // CO3: Push operation
    public void push(T item) {
        if (top == arr.length - 1) resize();
        arr[++top] = item;
    }

    // CO3: Pop operation
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        return arr[top--];
    }

    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return arr[top];
    }

    public boolean isEmpty() { return top == -1; }

    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newArr = (T[]) new Object[arr.length * 2];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        arr = newArr;
    }
}

// Custom exception for stack
class EmptyStackException extends RuntimeException { }

/**
 * CO3: Circular queue implementation using an array.
 * Simulates pending conversion requests.
 */
class Queue<T> {
    private T[] arr;
    private int front, rear, size;
    private static final int INITIAL_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public Queue() {
        arr = (T[]) new Object[INITIAL_CAPACITY];
        front = 0;
        rear = -1;
        size = 0;
    }

    // CO3: Enqueue operation
    public void enqueue(T item) {
        if (size == arr.length) resize();
        rear = (rear + 1) % arr.length;
        arr[rear] = item;
        size++;
    }

    // CO3: Dequeue operation
    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException();
        T item = arr[front];
        front = (front + 1) % arr.length;
        size--;
        return item;
    }

    public boolean isEmpty() { return size == 0; }

    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newArr = (T[]) new Object[arr.length * 2];
        for (int i = 0; i < size; i++) {
            newArr[i] = arr[(front + i) % arr.length];
        }
        front = 0;
        rear = size - 1;
        arr = newArr;
    }
}

/**
 * CO3: Max-heap implementation for prioritized processing.
 * Used to extract top N currencies by exchange rate.
 */
class MaxHeap {
    private Currency[] heap;
    private int size;
    private Comparator<Currency> comparator;

    public MaxHeap(int capacity, Comparator<Currency> comp) {
        heap = new Currency[capacity];
        size = 0;
        this.comparator = comp;
    }

    // CO3: Insert into heap
    public void insert(Currency currency) {
        if (size == heap.length) resize();
        heap[size] = currency;
        siftUp(size);
        size++;
    }

    // CO3: Extract maximum (priority queue operation)
    public Currency extractMax() {
        if (size == 0) return null;
        Currency max = heap[0];
        heap[0] = heap[--size];
        siftDown(0);
        return max;
    }

    public Currency[] getTopN(int n) {
        n = Math.min(n, size);
        Currency[] top = new Currency[n];
        for (int i = 0; i < n; i++) {
            top[i] = extractMax();
        }
        return top;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (comparator.compare(heap[index], heap[parent]) <= 0) break;
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        int left, right, largest;
        while (index < size) {
            left = 2 * index + 1;
            right = 2 * index + 2;
            largest = index;

            if (left < size && comparator.compare(heap[left], heap[largest]) > 0)
                largest = left;
            if (right < size && comparator.compare(heap[right], heap[largest]) > 0)
                largest = right;
            if (largest == index) break;
            swap(index, largest);
            index = largest;
        }
    }

    private void swap(int i, int j) {
        Currency temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void resize() {
        heap = Arrays.copyOf(heap, heap.length * 2);
    }
}

/**
 * CO4: Hash table with chaining for fast lookup by currency code.
 * Uses separate chaining to handle collisions.
 */
class HashTable {
    private static class Entry {
        String key;
        Currency value;
        Entry(String key, Currency value) { this.key = key; this.value = value; }
    }

    private LinkedList<Entry>[] buckets;  // using java.util.LinkedList for simplicity
    private int capacity = 16;

    @SuppressWarnings("unchecked")
    public HashTable() {
        buckets = (LinkedList<Entry>[]) new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new LinkedList<>();
        }
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    // CO4: Insert/update operation
    public void put(String key, Currency currency) {
        int index = hash(key);
        for (Entry e : buckets[index]) {
            if (e.key.equals(key)) {
                e.value = currency; // update
                return;
            }
        }
        buckets[index].add(new Entry(key, currency));
    }

    // CO4: Fast lookup (O(1) average)
    public Currency get(String key) {
        int index = hash(key);
        for (Entry e : buckets[index]) {
            if (e.key.equals(key)) return e.value;
        }
        return null;
    }

    public boolean containsKey(String key) {
        return get(key) != null;
    }
}

// ==================== ALGORITHM CLASSES ====================

/**
 * CO1: Implementation of classical sorting algorithms.
 * Bubble, Selection, Insertion, Merge, and Quick sort are provided.
 * Users can compare their efficiency based on input size.
 */
class Sorting {

    // CO1: Bubble sort
    public static void bubbleSort(Currency[] arr, Comparator<Currency> comp) {
        int n = arr.length;
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
                if (comp.compare(arr[j], arr[j+1]) > 0) {
                    swap(arr, j, j+1);
                }
            }
        }
    }

    // CO1: Selection sort
    public static void selectionSort(Currency[] arr, Comparator<Currency> comp) {
        int n = arr.length;
        for (int i = 0; i < n-1; i++) {
            int minIdx = i;
            for (int j = i+1; j < n; j++) {
                if (comp.compare(arr[j], arr[minIdx]) < 0) {
                    minIdx = j;
                }
            }
            swap(arr, i, minIdx);
        }
    }

    // CO1: Insertion sort
    public static void insertionSort(Currency[] arr, Comparator<Currency> comp) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            Currency key = arr[i];
            int j = i - 1;
            while (j >= 0 && comp.compare(arr[j], key) > 0) {
                arr[j+1] = arr[j];
                j--;
            }
            arr[j+1] = key;
        }
    }

    // CO1: Merge sort
    public static void mergeSort(Currency[] arr, Comparator<Currency> comp) {
        if (arr.length > 1) {
            Currency[] left = new Currency[arr.length / 2];
            Currency[] right = new Currency[arr.length - left.length];
            System.arraycopy(arr, 0, left, 0, left.length);
            System.arraycopy(arr, left.length, right, 0, right.length);
            mergeSort(left, comp);
            mergeSort(right, comp);
            merge(arr, left, right, comp);
        }
    }

    private static void merge(Currency[] result, Currency[] left, Currency[] right, Comparator<Currency> comp) {
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            if (comp.compare(left[i], right[j]) <= 0) {
                result[k++] = left[i++];
            } else {
                result[k++] = right[j++];
            }
        }
        while (i < left.length) result[k++] = left[i++];
        while (j < right.length) result[k++] = right[j++];
    }

    // CO1: Quick sort
    public static void quickSort(Currency[] arr, Comparator<Currency> comp, int low, int high) {
        if (low < high) {
            int pi = partition(arr, comp, low, high);
            quickSort(arr, comp, low, pi-1);
            quickSort(arr, comp, pi+1, high);
        }
    }

    private static int partition(Currency[] arr, Comparator<Currency> comp, int low, int high) {
        Currency pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comp.compare(arr[j], pivot) <= 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i+1, high);
        return i+1;
    }

    private static void swap(Currency[] arr, int i, int j) {
        Currency temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

/**
 * CO1: Implementation of linear and binary search algorithms.
 * Linear search works on unsorted data; binary search requires sorted input.
 */
class Searching {

    // CO1: Linear search
    public static Currency linearSearch(Currency[] arr, String code) {
        for (Currency c : arr) {
            if (c.getCode().equalsIgnoreCase(code)) return c;
        }
        return null;
    }

    // CO1: Binary search (requires sorted array by code)
    public static Currency binarySearch(Currency[] arr, String code) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = arr[mid].getCode().compareToIgnoreCase(code);
            if (cmp == 0) return arr[mid];
            if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return null;
    }
}

// ==================== CORE MANAGER CLASS ====================


class CurrencyManager {
    private HashTable currencyMap;          // CO4: Fast lookup
    private LinkedList<Currency> currencyList; // CO2: All currencies in order
    private Stack<ConversionRecord> historyStack; // CO3: Conversion history with undo
    private Queue<ConversionRecord> pendingQueue; // CO3: Pending conversion requests
    private Comparator<Currency> byCode = Comparator.comparing(Currency::getCode);
    private Comparator<Currency> byName = Comparator.comparing(Currency::getName);
    private Comparator<Currency> byRate = Comparator.comparing(Currency::getRateToUSD).reversed();

    public CurrencyManager() {
        currencyMap = new HashTable();
        currencyList = new LinkedList<>();
        historyStack = new Stack<>();
        pendingQueue = new Queue<>();
        // Preload popular currencies
        addCurrency(new Currency("USD", "US Dollar", 1.0));
        addCurrency(new Currency("EUR", "Euro", 0.92));
        addCurrency(new Currency("GBP", "British Pound", 0.79));
        addCurrency(new Currency("JPY", "Japanese Yen", 148.50));
        addCurrency(new Currency("INR", "Indian Rupee", 83.20));
        addCurrency(new Currency("CAD", "Canadian Dollar", 1.35));
        addCurrency(new Currency("AUD", "Australian Dollar", 1.52));
        addCurrency(new Currency("CHF", "Swiss Franc", 0.91));
        addCurrency(new Currency("CNY", "Chinese Yuan", 7.19));
        addCurrency(new Currency("NZD", "New Zealand Dollar", 1.65));
    }

    
    public void addCurrency(Currency c) {
        if (!currencyMap.containsKey(c.getCode())) {
            currencyList.add(c);
            currencyMap.put(c.getCode(), c);
        } else {
            System.out.println("Currency already exists.");
        }
    }

    // CO4: Fast lookup via hash table
    public Currency findCurrency(String code) {
        return currencyMap.get(code.toUpperCase());
    }

    public void updateRate(String code, double newRate) {
        Currency c = findCurrency(code);
        if (c != null) {
            c.setRateToUSD(newRate);
            System.out.println("Rate updated.");
        } else {
            System.out.println("Currency not found.");
        }
    }

   
    public double convert(String fromCode, String toCode, double amount) {
        Currency from = findCurrency(fromCode);
        Currency to = findCurrency(toCode);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Currency not found");
        }
        double amountInUSD = amount / from.getRateToUSD();
        double result = amountInUSD * to.getRateToUSD();
        ConversionRecord record = new ConversionRecord(fromCode, toCode, amount, result);
        historyStack.push(record);      // CO3: Push to history stack
        pendingQueue.enqueue(record);   // CO3: Enqueue for later processing
        return result;
    }

    // CO3: Display history using stack
    public void showHistory() {
        System.out.println("Conversion History (most recent first):");
        Stack<ConversionRecord> temp = new Stack<>();
        while (!historyStack.isEmpty()) {
            ConversionRecord rec = historyStack.pop();
            System.out.println(rec);
            temp.push(rec);
        }
        while (!temp.isEmpty()) {
            historyStack.push(temp.pop());
        }
    }

    // CO3: Undo last conversion (stack pop)
    public void undoLastConversion() {
        if (!historyStack.isEmpty()) {
            ConversionRecord last = historyStack.pop();
            System.out.println("Undone: " + last);
        } else {
            System.out.println("No conversions to undo.");
        }
    }

    // CO3: Process pending queue
    public void processPending() {
        while (!pendingQueue.isEmpty()) {
            ConversionRecord rec = pendingQueue.dequeue();
            System.out.println("Processing: " + rec);
        }
    }

    // CO1: Display currencies sorted using chosen algorithm
    public void displayCurrenciesSorted(String criteria, String algorithm) {
        Currency[] arr = new Currency[currencyList.size()];
        int i = 0;
        for (Currency c : currencyList) arr[i++] = c;

        Comparator<Currency> comp;
        switch (criteria.toLowerCase()) {
            case "name": comp = byName; break;
            case "rate": comp = byRate; break;
            default: comp = byCode;
        }

        switch (algorithm.toLowerCase()) {
            case "bubble": Sorting.bubbleSort(arr, comp); break;
            case "selection": Sorting.selectionSort(arr, comp); break;
            case "insertion": Sorting.insertionSort(arr, comp); break;
            case "merge": Sorting.mergeSort(arr, comp); break;
            case "quick": Sorting.quickSort(arr, comp, 0, arr.length-1); break;
            default: System.out.println("Unknown algorithm, using bubble."); Sorting.bubbleSort(arr, comp);
        }

        for (Currency c : arr) System.out.println(c);
    }

    // CO1: Search for a currency (linear or binary)
    public void searchCurrency(String code, boolean useBinary) {
        Currency[] arr = new Currency[currencyList.size()];
        int i = 0;
        for (Currency c : currencyList) arr[i++] = c;

        Currency result;
        if (useBinary) {
            Sorting.quickSort(arr, byCode, 0, arr.length-1); // sort for binary search
            result = Searching.binarySearch(arr, code);
        } else {
            result = Searching.linearSearch(arr, code);
        }

        if (result != null) {
            System.out.println("Found: " + result);
        } else {
            System.out.println("Currency not found.");
        }
    }

    // CO3: Use max-heap to get top N currencies by rate
    public void showTopCurrenciesByRate(int n) {
        MaxHeap heap = new MaxHeap(currencyList.size(), byRate);
        for (Currency c : currencyList) heap.insert(c);
        Currency[] top = heap.getTopN(n);
        System.out.println("Top " + n + " currencies by rate:");
        for (Currency c : top) System.out.println(c);
    }

    // Helper to get list of currencies for display
    public LinkedList<Currency> getCurrencyList() {
        return currencyList;
    }
}

// ==================== MAIN APPLICATION CLASS ====================


public class CurrencyConverter {
    public static void main(String[] args) {
        CurrencyManager manager = new CurrencyManager();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== SIMPLE CURRENCY CONVERTER =====");
            System.out.println("1. Convert Currency");
            System.out.println("2. View All Currencies");
            System.out.println("3. Add/Update Currency (Admin)");
            System.out.println("4. Show Conversion History");
            System.out.println("5. Undo Last Conversion");
            System.out.println("6. Show Top Currencies by Rate");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    // Direct conversion
                    System.out.println("\n--- Available Currencies ---");
                    LinkedList<Currency> currencies = manager.getCurrencyList();
                    // Convert to array for easy indexed access
                    Currency[] currArray = new Currency[currencies.size()];
                    int idx = 0;
                    for (Currency c : currencies) {
                        currArray[idx++] = c;
                        System.out.println((idx) + ". " + c.getCode() + " - " + c.getName());
                    }

                    System.out.print("\nSelect source currency (number): ");
                    int fromIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    if (fromIdx < 0 || fromIdx >= currArray.length) {
                        System.out.println("Invalid selection.");
                        break;
                    }
                    Currency from = currArray[fromIdx];

                    System.out.print("Select target currency (number): ");
                    int toIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    if (toIdx < 0 || toIdx >= currArray.length) {
                        System.out.println("Invalid selection.");
                        break;
                    }
                    Currency to = currArray[toIdx];

                    System.out.print("Enter amount: ");
                    double amount = sc.nextDouble();
                    sc.nextLine();

                    try {
                        double result = manager.convert(from.getCode(), to.getCode(), amount);
                        System.out.printf("\n%.2f %s = %.2f %s\n", amount, from.getCode(), result, to.getCode());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Conversion error: " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.println("\n--- All Currencies ---");
                    for (Currency c : manager.getCurrencyList()) {
                        System.out.println(c);
                    }
                    break;

                case 3:
                    // Admin: add or update currency
                    System.out.print("Enter currency code (e.g., USD): ");
                    String code = sc.nextLine().toUpperCase();
                    System.out.print("Enter currency name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter exchange rate (1 USD = ? of this currency): ");
                    double rate = sc.nextDouble();
                    sc.nextLine();

                    Currency existing = manager.findCurrency(code);
                    if (existing == null) {
                        manager.addCurrency(new Currency(code, name, rate));
                        System.out.println("Currency added.");
                    } else {
                        manager.updateRate(code, rate);
                    }
                    break;

                case 4:
                    manager.showHistory();
                    break;

                case 5:
                    manager.undoLastConversion();
                    break;

                case 6:
                    System.out.print("How many top currencies? ");
                    int n = sc.nextInt();
                    sc.nextLine();
                    manager.showTopCurrenciesByRate(n);
                    break;

                case 7:
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}