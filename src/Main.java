import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class Exam {
    String predmet;
    int ocenka;

    public Exam(String predmet, int ocenka) {
        this.predmet = predmet;
        this.ocenka = ocenka;
    }
}

class Zachet {
    String predmet;
    boolean sdan;

    public Zachet(String predmet, boolean sdan) {
        this.predmet = predmet;
        this.sdan = sdan;
    }
}

class Zachetka {
    String familia;
    String imya;
    String otchestvo;
    int kurs;
    String gruppa;
    List<Session> sessions = new ArrayList<>();

    public Zachetka(String familia, String imya, String otchestvo, int kurs, String gruppa) {
        this.familia = familia;
        this.imya = imya;
        this.otchestvo = otchestvo;
        this.kurs = kurs;
        this.gruppa = gruppa;
    }

    class Session {
        int nomer;
        List<Exam> exams = new ArrayList<>();
        List<Zachet> zachets = new ArrayList<>();

        public Session(int nomer) {
            this.nomer = nomer;
        }

        public void addExam(String predmet, int ocenka) {
            exams.add(new Exam(predmet, ocenka));
        }

        public void addZachet(String predmet, boolean sdan) {
            zachets.add(new Zachet(predmet, sdan));
        }

        public boolean isOtlichnik() {
            for (Exam e : exams) {
                if (e.ocenka < 9) return false;
            }
            for (Zachet z : zachets) {
                if (!z.sdan) return false;
            }
            return true;
        }

        public boolean isUspevaet() {
            for (Exam e : exams) {
                if (e.ocenka < 4) return false;
            }
            for (Zachet z : zachets) {
                if (!z.sdan) return false;
            }
            return true;
        }

        public double getSredniyBall() {
            if (exams.isEmpty()) return 0;
            double sum = 0;
            for (Exam e : exams) {
                sum += e.ocenka;
            }
            return sum / exams.size();
        }
    }

    public boolean isOtlichnik() {
        if (sessions.isEmpty()) return false;
        for (Session s : sessions) {
            if (!s.isOtlichnik()) return false;
        }
        return true;
    }

    public boolean isUspevaet() {
        if (sessions.isEmpty()) return false;
        for (Session s : sessions) {
            if (!s.isUspevaet()) return false;
        }
        return true;
    }
}

public class Main {
    public static void main(String[] args) {
        List<Zachetka> vedomost = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt"), StandardCharsets.UTF_8))) {
            String line;
            Zachetka currentZachetka = null;
            Zachetka.Session currentSession = null;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[0];

                if (type.equals("STUDENT")) {
                    currentZachetka = new Zachetka(parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), parts[5]);
                    vedomost.add(currentZachetka);
                } else if (type.equals("SESSION") && currentZachetka != null) {
                    currentSession = currentZachetka.new Session(Integer.parseInt(parts[1]));
                    currentZachetka.sessions.add(currentSession);
                } else if (type.equals("EXAM") && currentSession != null) {
                    currentSession.addExam(parts[1], Integer.parseInt(parts[2]));
                } else if (type.equals("ZACHET") && currentSession != null) {
                    currentSession.addZachet(parts[1], Boolean.parseBoolean(parts[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
            return;
        }

        try (PrintWriter pw1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("output1.txt"), StandardCharsets.UTF_8))) {
            for (Zachetka z : vedomost) {
                if (z.isOtlichnik()) {
                    for (Zachetka.Session s : z.sessions) {
                        for (Exam e : s.exams) {
                            pw1.printf("%s %s %s, %d курс, группа %s, сессия %d, %s, оценка: %d\n",
                                    z.familia, z.imya, z.otchestvo, z.kurs, z.gruppa, s.nomer, e.predmet, e.ocenka);
                        }
                        for (Zachet zachet : s.zachets) {
                            pw1.printf("%s %s %s, %d курс, группа %s, сессия %d, %s, зачет сдан\n",
                                    z.familia, z.imya, z.otchestvo, z.kurs, z.gruppa, s.nomer, zachet.predmet);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи в output1.txt: " + e.getMessage());
        }

        try (PrintWriter pw2 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("output2.txt"), StandardCharsets.UTF_8))) {
            for (Zachetka z : vedomost) {
                if (z.isUspevaet()) {
                    pw2.printf("%s %s %s:\n", z.familia, z.imya, z.otchestvo);
                    for (Zachetka.Session s : z.sessions) {
                        pw2.printf("  Сессия %d: Средний балл = %.2f\n", s.nomer, s.getSredniyBall());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи в output2.txt: " + e.getMessage());
        }

        System.out.println("Программа успешно завершена. Проверьте файлы output1.txt и output2.txt");
    }
}