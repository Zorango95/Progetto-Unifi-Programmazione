package gj.forza4.player;

import java.util.Random;

/**
 * 
 * @author D'Autilia Mattia
 *
 * <p>La classe Dautilia, contiene tutta la strategia del giocatore. Tale strategia,
 * si basa sul calcolo delle colonne con posizoni libere sulle quale inserire la pedina per 
 * effettuare un Forza4 e sceglie una tra queste ma non una colonna qualsiasi, bensì la colonna con una posizione libera
 * che o porta alla realizzazione di un nostro Forza4 quindi porta alla vittoria o blocca un Forza4 
 * avversario impedendoli di vincere. In poche parole calcolo quale colonna ha una priorità maggiore sia per noi che per 
 * il giocatore avversario.
 * 
 */
public class Dautilia implements Player {
	private int i; // dimensione tab righe
	private int j; // dimensione tab colonne
	private int[][] mat; // matrice
	private int[] col; // vettore colonne
	private Random random = new Random();

	/**
	 * Costruttore di default.
	 * 
	 */
	public Dautilia() {

	}

	/**
	 * Il metodo start, viene richiamto dal Game Manager, il quale gli comunica
	 * due valori interi, nr e nc, che servono a inizializzare la tabella
	 * (matrice) sulla quale viene impostato il gioco Forza4 e il vettore
	 * colonne che serve alla nostra strategia; tale metodo viene richiamato dal
	 * Game Manager all'inizio di ogni partita per un tot di partite configurate
	 * inizialmente.
	 * 
	 * @param nr
	 *            numero righe.
	 * @param nc
	 *            numero colonne.
	 */
	public void start(int nr, int nc) {
		i = nr;
		j = nc;
		mat = new int[i][j];
		col = new int[j];
		for (int y = 0; y <= i - 1; y++) { // inizializza la matrice con 0, cioe vuota
			for (int l = 0; l <= j - 1; l++) {
				mat[y][l] = 0;
			}
		}
		for (int y = 0; y <= j - 1; y++) { // inizializza il vettore colonne con 0
			col[y] = 0;
		}
	}

	/**
	 * Il metodo Move, viene richiamto dal Game Manager, e contiene tutta la
	 * strategia; inizializza due vettori, att e def, che conterranno per ogni pedina che ad ogni passo saranno contenute nella tabella, le
	 * colonne con posizioni libere nelle quali inserire una nuova pedina; in base alla loro grandezza,
	 * noi faremo una ricerca per selezionare un unico elemento (colonna) o dal vettore att o dal vettore def, prima controllando se o noi o il nostro avversario fa Forza4;
	 * altrimenti si eliminano dai vettori le colonne che se occupate da una pedina possono portare
	 * una possibile vittoria dell'avversario o nostra. Dopo di chè, controlliamo le lunghezze dei vettori, se att è maggiore di def, si sceglie una elemento del vettore att che sarà la colonna con posizioni libere inviata al GameManager;
	 * altrimenti se def è maggiore di att, si sceglie una elemento del vettore def che sarà la colonna con posizioni libere inviata al GameManager; 
	 * se att e def hanno la stessa lunghezza si uniscono i vettori e si sceglie un elemento a random nel vettore unito che sarà la colonna con posizioni libere inviata al GameManager. 
	 * La colonna con posizione libere scelta sarà la colonna di ritorno del metodo move , dato al Game Manager; prima di fare il
	 * return, in base alla colonna selezionata come nostra mossa, si aggiorna
	 * anche la nostra matrice.
	 * 
	 * @return x colonna dove inseriamo la pedina
	 */
	public int move() {
		int x = -1; // inizailizzo futuro valore di ritorno
		int[] att = new int[0]; // vettore colonne attacco
		int[] def = new int[0]; // vettore colonne difesa
		for (int y = 0; y <= i - 1; y++) { // visione matrice per righe
			for (int l = 0; l <= j - 1; l++) {
				if (mat[y][l] == 1) {
					int[] attProv = strategyF4(y, l, 1); // se trova una pedia 1 va in Attacco
					att = uniteVector(att, attProv); // unisce vettore soluzioni in vettore attacco
				}
				if (mat[y][l] == 2) {
					int[] defProv = strategyF4(y, l, 2); // se trova una pedia 2 va in Difesa
					def = uniteVector(def, defProv); // unisce vettore soluzioni in vettore difesa colonne
				}
			}
		}
		int[] att2 = new int[0];
		att2 = uniteVector(att2, att);
		int[] def2 = new int[0];
		def2 = uniteVector(def2, def);
		int a = connect4(att2, 1);
		int d = connect4(def2, 2);
		if (a != -1) {
			x = a;
			mat[col[x]][x] = 1; // aggiorna matrice
			col[x] = col[x] + 1;
		} else {
			if (d != -1) {
				x = d;
				mat[col[x]][x] = 1; // aggiorna matrice
				col[x] = col[x] + 1;
			}
		}
		if (x == -1) {
			int[] at = tabooF4(uniteVector(att, def), 1);
			int[] dt = tabooF4(uniteVector(att, def), 2);
			int[] tabooa = uniteVector(at, dt);
			int[] tabood = uniteVector(at, dt);
			att = vectorProper(tabooa, att);
			def = vectorProper(tabood, def);
			if (att.length == 0 && def.length == 0) { // controlla se non ci sono pedine soluzioni attacco o difesa
				boolean trov = false;
				for (int y = 0; y <= i - 1 && trov == false; y++) {
					for (int l = 0; l <= j - 1 && trov == false; l++) {
						if (mat[y][l] != 0) {
							trov = true;
						}
					}
				}
				if (trov == true) { // se non trova nessuna pedina, inserisce pedina 1 nella posizione 3 centrale, altrimenti inserisci pedina 1 nella prima posizione libera
					int contatore = 0;
					int[] temp = new int[contatore];
					for (int f = 0; f <= col.length - 1; f++) {
						if (col[f] <= i - 1) {
							if (contatore > temp.length - 1) { // copia elementi in un vettore provvisorio
								int[] prov = new int[temp.length + 1];
								for (int j = 0; j < temp.length; j++) {
									prov[j] = temp[j];
								}
								temp = prov; // copia gli elementi dal vettore prov provvisorio nel vettore posrighe
							}
							temp[contatore] = f; // salva nel vettore posrighe, la colonna destra cd
							contatore = contatore + 1;
						}
					}
					int[] provat = tabooF4(temp, 1);
					int[] provdt = tabooF4(temp, 2);
					temp = vectorProper(uniteVector(provat, provdt), temp);
					if (temp.length == 0) {
						int ri = 0;
						boolean lib = false;
						for (int f = 0; f <= col.length - 1 && lib == false; f++) {
							if (col[f] <= i - 1) {
								ri = col[f];
								x = f;
								lib = true;
							}
						}
						mat[ri][x] = 1; // aggiorna matrice
						col[x] = col[x] + 1;
					} else {
						x = searchElem(temp);
						mat[col[x]][x] = 1; // aggiorna matrice
						col[x] = col[x] + 1;
					}
				} else {
					int n = j - 1;
					x = random.nextInt(n);
					mat[col[x]][x] = 1; // aggiorna matrice
					col[x] = col[x] + 1;
				}
			}
			if (att.length > def.length) { // controlla se vettore att è maggiore del vettore difesa sceglie colonna att
				x = searchElem(att);
				mat[col[x]][x] = 1; // aggiorna matrice
				col[x] = col[x] + 1;
			}
			if (att.length < def.length) { // controlla se vettore att e minore del vettore difesa sceglie colonna def
				x = searchElem(def);
				mat[col[x]][x] = 1; // aggiorna matrice
				col[x] = col[x] + 1;
			}
			if ((att.length != 0 && def.length != 0)
					&& (att.length == def.length)) { // controlla se vettore att é uguale del vettore difesa sceglie colonna piu ripetuta
				int[] b = uniteVector(att, def);
				x = searchElem(b);
				mat[col[x]][x] = 1; // aggiorna matrice
				col[x] = col[x] + 1;
			}
		}
		return x;
	}

	/**
	 * Il metodo connect4, ricerca l'elemento (colonna) ripetuto più di tre volte,
	 * richiamando il metodo controlMatrixF4,e restituisce la colonna libera con la
	 * quale si puo fare forza4, sia dell'attacco sia della difesa (attacco avversatio).
	 * 
	 * @param p
	 *            valore pedina
	 * @param v
	 *            vettore att2 o def2 su cui effetturare la ricerca
	 * 
	 * @return k colonna ripetuta
	 */
	private int connect4(int[] v, int p) { // ricerca elemento forza4
		int k = -1;// assegno a k un valore negativo
		int contr = 0;
		boolean trov = false;
		for (int i = 0; i <= v.length - 1 && trov == false; i++) { // visita il vettore
			int conta = 1;
			if (v[i] != -1) {
				for (int j = i + 1; j <= v.length - 1; j++) { // elementi trovati assegna -1
					if (v[i] == v[j]) {
						conta = conta + 1;
						v[j] = -1;
					}
				}
			}
			if (conta >= 3) {
				int x = v[i];
				trov = controlMatrixF4(x, p, contr);
				if (trov == true) {
					k = x;
				}
			}
		}
		return k;
	}

	/**
	 * Il metodo tabooF4, ricerca l'elemento (colonna) ripetuto più di tre volte,
	 * richiamando il metodo controlMatrixF4,e restituisce la colonna libera(aumentando
	 * la riga di uno), con la quale si puo fare un forza4, sia dell'attacco sia
	 * della difesa (attacco avversario), nella mossa successiva.
	 * 
	 * @param p
	 *            valore pedina
	 * @param v
	 *            vettore att o def su cui effetturare la ricerca
	 * 
	 * @return g vettore che contiene le colonne vuote (con la riga umentata di
	 *         uno) che non si devono utilizzare
	 */
	private int[] tabooF4(int[] v, int p) { // ricerca colonne forza4 successive
		int r=i;
		int contatore = 0;
		int[] g = new int[contatore];
		int contr = 1;
		boolean trov = false;
		for (int i = 0; i <= v.length - 1; i++) { // visita il vettore
			if (col[v[i]] < r-1) {
				trov = controlMatrixF4(v[i], p, contr);
				if (trov == true) {
					if (contatore >= g.length - 1) { // copia elementi in un vettore provvisorio
						int[] prov = new int[g.length + 1];
						for (int j = 0; j < g.length; j++) {
							prov[j] = g[j];
						}
						g = prov; // copia gli elementi dal vettore prov provvisorio nel vettore g
					}
					g[contatore] = v[i]; // salva nel vettore g, la colonna
					contatore = contatore + 1;
				}
			}
		}
		return g;
	}

	/**
	 * Il metodo controlMatrixF4, riceve la colonna libera e la pedina 1 o 2, e
	 * assegna a tre contatori(cc,cr,cdd), tramite tre metodi, tre valori e se
	 * uno di questi tre valori è maggiore o uguale a 3, la colonna x permette un
	 * forza4.
	 * 
	 * @param x
	 *            colonna libera per il possibile forza4 dell'attacco o della
	 *            difesa
	 * @param p
	 *            pedina 1 o 2
	 * 
	 * @param contr
	 *            variabile di controllo per differenziare connectF4 da tabooF4
	 * 
	 * @return t variabile booleana true=forza4 false=noforza4
	 */
	private boolean controlMatrixF4(int x, int p, int contr) {
		boolean t = false;
		int cr = countsRows(x, p, contr);     			// conta righe 
		int cdd = countsDiagonals(x, p, contr);			// conta diagonale
		if (contr == 0) {								// contr==0 è connectF4 
			int cc = countsColumns(x, p);				// conta colonne
			if (cr >= 3 || cc >= 3 || cdd >= 3) {
				t = true;
			}
		}
		if (contr == 1) {								// contr==1 è tabooF4
			if (cr >= 3 || cdd >= 3) {
				t = true;
			}
		}
		return t;
	}

	/**
	 * Il metodo searchElem, attua una prima ricerca o nel vettore att o nel vettore
	 * def, e restituisci un valore max che corrisponde al numero che si trova all'interno del vettore, che si ripete di più; poi attua una seconda ricerca
	 * sullo stesso vettore att o def (infatti inizialmente facciamo due copie dei vettori) per contare quanti elementi sono ripetuti
	 * un numero di volte quanto il valore max e restituisce un valore cc; infine crea un vettore di grandezza cc, e ci inserisce tutti gli elementi ripetuti
	 * un max di volte e restituisce a random uno di questi, che corrisponderebbe alla colonna scelta dalla ricerca. 
	 * 
	 * @param v
	 *            vettore att o def
	 * 
	 * @return scelta colonna più volte ripetuta nel vettore att o deff
	 */
	private int searchElem(int[] v) { // ricerca elemento
		int[] prov = new int[0];
		int[] prov2 = new int[0];
		prov2 = uniteVector(prov2, v);
		prov = uniteVector(prov, v);
		int max = 0; // valore max
		int scelta = 0; // elemento scelto
		for (int i = 0; i <= v.length - 1; i++) { // visita il vettore
			int conta = 1;
			if (v[i] != -1) {
				for (int j = i + 1; j <= v.length - 1; j++) { // ogni elemento ripetuto assegna -1
					if (v[i] == v[j]) {
						conta = conta + 1;
						v[j] = -1;
					}
				}
			}
			if (conta > max) {
				max = conta;
			}
		}
		int cc = 0;
		for (int i = 0; i <= prov.length - 1; i++) { // visita il vettore
			int conta = 1;
			if (prov[i] != -1) {
				for (int j = i + 1; j <= prov.length - 1; j++) { // ogni elemento ripetuto assegna -1
					if (prov[i] == prov[j]) {
						conta = conta + 1;
						prov[j] = -1;
					}
				}
			}
			if (conta == max) {
				cc++;
			}
		}
		int[] fin = new int[cc];
		int l = 0;
		for (int i = 0; i <= prov2.length - 1 && l <= fin.length - 1; i++) { // visita il vettore
			int conta = 1;
			if (prov2[i] != -1) {
				for (int j = i + 1; j <= prov2.length - 1; j++) { // ogni elemento ripetuto assegna -1
					if (prov2[i] == prov2[j]) {
						conta = conta + 1;
						prov2[j] = -1;
					}
				}
			}
			if (conta == max) {
				fin[l] = prov2[i];
				l = l + 1;
			}
		}
		int f = random.nextInt(fin.length);
		scelta = fin[f];
		return scelta;
	}

	/**
	 * Il metodo uniteVector, unisci due vettori, in un altro vettore b che avrà 
	 * una lunghezza che sarà la somma delle lunghezze dei vettori att e def.
	 * 
	 * @param v
	 *            vettore da unire
	 * @param a
	 *            vettore da unire
	 * 
	 * @return b vettore a, unito al vettore v
	 */
	private int[] uniteVector(int[] v, int[] a) { // unisci due vettori
		int[] b = new int[v.length + a.length]; // assegna a vettore b la somma delle lunghezze del vettore v e a
		int l = 0;
		for (int i = 0; i <= v.length - 1; i++) { // copia gli elementi del vettore v nel vettore b
			b[i] = v[i];
			l = i;
		}
		l = v.length;
		for (int j = 0; j <= a.length - 1; j++) { // copia gli elementi del vettore a nel vettore b
			b[l] = a[j];
			l = l + 1;
		}
		return b;
	}

	/**
	 * Il metodo vectorProper, prende gli elementi(colonne) di un vettore, uno alla
	 * volta e se questo elemento è presente nell'altro vettore, da quest'ultimo,
	 * l'elemento(colonna) viene cancellato. In questo metodo vengono cancellati gli elementi uguali 
	 * e il ritorno è il vettore v modificato.
	 * 
	 * @param c
	 *            vettore taboo
	 * @param v
	 *            vettore da modificare
	 * 
	 * @return v vettore modificato
	 */
	private int[] vectorProper(int[] c, int[] v) {
		for (int i = 0; i < c.length; i++) {
			for (int y = 0; y < v.length; y++) {
				if (v[y] == c[i]) {
					v[y] = -1;
				}
			}
		}
		int contatore = 0;
		int[] provv = new int[contatore];
		for (int a = 0; a < v.length; a++) {
			if (v[a] != -1) {
				if (contatore > provv.length - 1) {
					int[] temp = new int[provv.length + 1];
					for (int j = 0; j < provv.length; j++) {
						temp[j] = provv[j];
					}
					provv = temp;
				}
				provv[contatore] = v[a];
				contatore = contatore + 1;
			}
		}
		v = provv;
		return v;
	}

	/**
	 * Il metodo countsRows, è simile al metodo strategyRows, la differenza sta
	 * nel fatto che restituisce un contatore che si aggiorna ogni qual volta
	 * che il controllo trova su quella riga una pedina uguale a quella cercata.
	 * 
	 * @param c
	 *            colonna libera per il possibile forza4 dell'attacco o della
	 *            difesa
	 * @param p
	 *            pedina 1 o 2
	 * 
	 * @param contr
	 *            variabile di controllo per differenziare connectF4 da tabooF4
	 * 
	 * @return cr contatore pedine righe
	 */
	private int countsRows(int c, int p, int contr) {
		int cr = 0;
		int des = c;
		int sin = c;
		int riga = -1;
		if (contr == 0) {
			riga = col[c];		// connectF4
		} else {
			riga = col[c] + 1;	//	tabooF4
		}
		boolean t = true;
		if (des == j - 1) { 
			t = false;
		}
		while (des < j - 1 && t == true) {
			if (mat[riga][des + 1] == p) { // se il posto accanto è occupato da una pedina corrispondete a quella cercata
				cr = cr + 1; // si aumenta il contatore cr di 1
			} else {
				t = false;
			}
			des = des + 1;
		}
		t = true;
		if (sin == 0) { 
			t = false;
		}
		while (sin > 0 && t == true) {
			if (mat[riga][sin - 1] == p) { // se il posto accanto  è occupato da una pedina corrispondete a quella cercata
				cr = cr + 1; // si aumenta il contatore cr di 1
			} else {
				t = false;
			}
			sin = sin - 1;
		}
		return cr;
	}

	/**
	 * Il metodo countsColumns, è simile al metodo strategyColumns, la differenza
	 * sta nel fatto che restituisce un contatore che si aggiorna ogni qual
	 * volta che il controllo trova su quella colonna una pedina uguale a quella
	 * cercata.
	 * 
	 * @param c
	 *            colonna libera per il possibile forza4 dell'attacco o della
	 *            difesa
	 * @param p
	 *            pedina 1 o 2
	 * @return cc contatore pedine colonne
	 */
	private int countsColumns(int c, int p) {
		int cc = 0;
		int riga = col[c];
		boolean t = true;
		if (riga == 0) { // se la riga presa in cosiderazione è uguale a 0 non si fa un controllo sotto
			t = false; 
		}
		while (riga > 0 && t == true) { // se la riga è maggiore si controlla se è uguale alla p e si incrementa il contatore cc
			if (mat[riga - 1][c] == p) {
				cc = cc + 1;
			} else {
				t = false;
			}
			riga = riga - 1;
		}
		return cc;
	}

	/**
	 * Il metodo countsDiagonals, è simile al metodo strategyDiagonals, la
	 * differenza sta nel fatto che restituisce un contatore, che è la somma di
	 * quattro contatori diversi per ogni diagonale, che si aggiornano ogni qual
	 * volta che il controllo trova su quella specifica diagonale una pedina
	 * uguale a quella cercata.
	 * 
	 * @param c
	 *            colonna libera per il possibile forza4 dell'attacco o della
	 *            difesa
	 * @param p
	 *            pedina 1 o 2
	 * 
	 * @param contr
	 *            variabile di controllo per differenziare connectF4 da tabooF4
	 * 
	 * @return conta contatore pedine diagonali
	 */
	private int countsDiagonals(int c, int p, int contr) {
		int cdds = 0; // contatore diagonale destra sopra
		int cddg = 0; // contatore diagonale destra sotto
		int cdss = 0; // contatore diagonale sinistra sotto
		int cdsg = 0; // contatore diagonale sinistra sotto
		boolean t = true;
		int d = c; // colonna verso destra
		int s = c; // colonna verso sinistra
		int su = -1;
		int g = -1;
		if (contr == 0) {
			su = col[c];
			g = col[c];
		} else {
			su = col[c] + 1;
			g = col[c] + 1;
		}
		if (d == j - 1) { // diagonale destra
			t = false;
		}
		while (d < j - 1 && su < i - 1 && t == true) { // diagonale destra sopra
			if (mat[su + 1][d + 1] == p) {
				cdds = cdds + 1;
			} else {
				t = false;
			}
			d = d + 1;
			su = su + 1;
		}
		d = c;
		t = true;
		if (g < 1) {
			t = false;
		}
		while (d < j - 1 && g > 0 && t == true) { // diagonale destra sotto
			if (mat[g - 1][d + 1] == p) {
				cddg = cddg + 1;
			} else {
				t = false;
			}
			d = d + 1;
			g = g - 1;
		}
		t = true;
		if (contr == 0) {
			su = col[c];
			g = col[c];
		} else {
			su = col[c] + 1;
			g = col[c] + 1;
		}
		if (s == 0) { // diagonale sinistra
			t = false;
		}
		while (s > 0 && su < i - 1 && t == true) { // diagonale sinistra sopra
			if (mat[su + 1][s - 1] == p) {
				cdss = cdss + 1;
			} else {
				t = false;
			}
			s = s - 1;
			su = su + 1;
		}
		t = true;
		s = c;
		if (g < 1) {
			t = false;
		}
		while (s > 0 && g > 0 && t == true) { // diagonale sinistra sotto
			if (mat[g - 1][s - 1] == p) {
				cdsg = cdsg + 1;
			} else {
				t = false;
			}
			s = s - 1;
			g = g - 1;
		}
		int conta1 = cdds + cdsg;
		int conta2 = cddg + cdss;
		if (conta1 >= 3) {
			return conta1;
		} else {
			if (conta2 >= 3) {
				return conta2;
			} else {
				int conta3 = 0;
				return conta3;
			}
		}
	}

	/**
	 * Il metodo strategyF4, racchiude 3 metodi procedurali, strategyRows,
	 * strategyColumns e strategyDiagonals, e deve unendo questi vettori,
	 * restituire un vettore, vettcontr che contiene le colonne vuote del nostro
	 * attacco o dell'attacco avversario.
	 * 
	 * @param r
	 *            numero riga pedina
	 * @param c
	 *            numero colonna pedina
	 * @param p
	 *            valore pedina 1 o 2
	 * @return vettcontr vettore contenente colonne att o def dipende se la
	 *         pedina è 1 o 2
	 */
	private int[] strategyF4(int r, int c, int p) { // mosse Attacco
		int[] attrighe = strategyRows(r, c, p); // vettore attrighe con colonne, dell'attacco righe
		int[] attcolonne = strategyColumns(r, c, p); // vettore attcolonne con colonne, dell'attacco colonna
		int[] attdiagonali = strategyDiagonals(r, c, p); // vettore attdiagonali con colonne, dell'attacco diagonali
		int[] vettcontr = uniteVector(uniteVector(attrighe, attcolonne),
				attdiagonali); // unione in vettcontr, dei vettori attrighe, attcolonne e attdiagonali
		return vettcontr;
	}

	/**
	 * Il metodo strategyRows, partendo dalla posizione della pedina presa in
	 * considerazione 1 o 2, conta quante possibilita si ha per aggiungere una
	 * pedina del proprio valore prima a destra e poi a sinistra, e se tale
	 * contatore è maggiore o uguale a tre, (considerando la tabella con i
	 * valori standard 6 X 7) si può aggiungere una pedina a destra se cd è
	 * diverso da -1, cioè se il metodo positionRowRight richiamato ha trovato
	 * una posizione libera, e una pedina a sinstra se cs è diverso da -1, cioè
	 * se il metodo strategyRowLeft richiamato ha trovato una posizione libera e
	 * salva questi valori nel vettore posrighe.
	 * 
	 * @param r
	 *            numero riga pedina
	 * @param c
	 *            numero colonna pedina
	 * @param p
	 *            valore pedina 1 o 2
	 * @return posrighe che contiene le colonne libere a destra o sinistra della
	 *         pedina 1 o 2 presa in considerazione
	 */
	private int[] strategyRows(int r, int c, int p) { // mosse attacco su riga
		int crd = 0; // contatore riga destra
		int crs = 0; // contatore riga sinistra
		boolean t = true; // valore di controllo
		int d = c; // indice riga destra
		int s = c; // indice riga sinistra
		if (d == j - 1) { // controllo riga destra, se è uguale a 6 non deve andare a destre
			t = false;
		}
		while (d < j - 1 && t == true) {
			if (r > 0) { // se la riga è maggiore di 0 bisogna fare un controllo sulla riga sottostante alla pedina presa in cosiderazione
				if (mat[r - 1][d + 1] != 0) {
					if (mat[r][d + 1] == 0 || mat[r][d + 1] == p) { // se il posto accanto è libero o vuoto
						crd = crd + 1; // si aumenta il contatore crd di 1
					} else {
						t = false;
					}
				} else {
					t = false;
				}
			}
			if (r == 0) { // se la riga è uguale a 0 basta il semplice controllo a destra
				if (mat[r][d + 1] == 0 || mat[r][d + 1] == p) { // se il posto accanto è libero o vuoto
					crd = crd + 1; // si aumenta il contatore crd di 1
				} else {
					t = false;
				}
			}
			d = d + 1;
		}
		int cd = -1; // colonna destra
		if (crd != 0) {
			cd = positionRowRight(r, c, p); // posizione colonna destra
		}
		t = true;
		if (s == 0) { // controllo riga sinsitra, se è uguale a 0 non deve andare a sinistra
			t = false;
		}
		while (s > 0 && t == true) {
			if (r > 0) { // se la riga è maggiore di 0 bisogna fare un controllo sulla riga sottostante alla pedina presa in considerazione
				if (mat[r - 1][s - 1] != 0) {
					if (mat[r][s - 1] == 0 || mat[r][s - 1] == p) { // se il posto accanto è libero o è uguale alla pedina che si è presa in considerazione
						crs = crs + 1; // si aumenta il contatore crs di 1
					} else {
						t = false;
					}
				} else {
					t = false;
				}
			}
			if (r == 0) { // se la riga è uguale a 0 basta il semplice controllo a sinistra
				if (mat[r][s - 1] == 0 || mat[r][s - 1] == p) { // se il posto accanto è libero o è uguale alla pedina che si è presa in considerazione
					crs = crs + 1; // si aumenta il contatore crs di 1
				} else {
					t = false;
				}
			}
			s = s - 1;
		}
		int cs = -1; // colonna sinistra
		if (crs != 0) {
			cs = positionRowLeft(r, c, p); // posizione colonna sinistra
		}

		int contatore = 0; // posizioni del vettore
		int[] posrighe = new int[contatore]; // vettore contenente le colonne dell'attacco riga

		if (crd + crs >= 3) { // se il contatore destra + contatore sinistra è maggiore o uguale a 3, cioè è possibile raggiungere un forza4 a destra o sinistra
			if (cd != -1) { // cd, colonna destra, contiene la colonna libera se c'è da destra
				if (contatore > posrighe.length - 1) { // copia elementi in un vettore provvisorio
					int[] prov = new int[posrighe.length + 1];
					for (int j = 0; j < posrighe.length; j++) {
						prov[j] = posrighe[j];
					}
					posrighe = prov; // copia gli elementi dal vettore prov provvisorio nel vettore posrighe
				}
				posrighe[contatore] = cd; // salva nel vettore posrighe, la colonna destra cd
				contatore = contatore + 1;
			}
			if (cs != -1) { // cs, colonna sinistra, contiene la colonna libera se c'è da sinistra
				if (contatore > posrighe.length - 1) { // copia elementi in un vettore provvisorio
					int[] prov = new int[posrighe.length + 1];
					for (int j = 0; j < posrighe.length; j++) {
						prov[j] = posrighe[j];
					}
					posrighe = prov; // copia gli elementi dal vettore prov provvisorio nel vettore posrighe
				}
				posrighe[contatore] = cs; // salva nel vettore posrighe, la colonna sinistra cs
				contatore++;
			}
		}
		return posrighe; // posizione/i attacco righe
	}

	/**
	 * Il metodo positionRowRight, controlla se a partire dalla pedina 1 o 2
	 * presa in cosiderazione, c'è una posizione libera a destra e se c'è
	 * bisogna prenderla come scelta e assegnarla a d, sempre che prima non si
	 * trova una posizione occupata da una pedina contraria a quella dalla quale
	 * stiamo partendo con il controllo, in tal caso si assegna a scelta -1.
	 * 
	 * @param r
	 *            numero riga pedina
	 * @param c
	 *            numero colonna pedina
	 * @param p
	 *            valore pedina 1 o 2
	 * @return d variabile che contiene il numero della colonna libera a destra
	 *         della oedina 1 o 2 presa in cosiderazione
	 */
	private int positionRowRight(int r, int c, int p) { // calcolo posizionecolonna destra
		int d = c; // indice riga destra
		boolean t = true;
		int scelta = -2; // scelta finale colonna destra
		while (d < j - 1 && scelta == -2) { // controllo prima se la colonna d è minore di 7
			if (r > 0) { // se la riga è maggiore di 0 bisogna fare un controllo sulla riga sottostante alla pedina presa in cosiderazione
				if ((mat[r - 1][d + 1] != 0) && t == true) {
					if (mat[r][d + 1] != 0 && t == true) { // se il posto accanto è libero, prendo quel posto come scelta di destra
						if (mat[r][d + 1] != p) {
							scelta = -1; // altrimenti assegno -1
							t = false;
						}
					} else {
						scelta = d + 1;
					}
				} else {
					t = false;
					scelta = -1;
				}
			}
			if (r == 0) { // se la riga è uguale a 0 basta il semplice controllo a destra
				if (mat[r][d + 1] != 0 && t == true) { // se il posto accanto è libero, prendo quel posto come scelta di destra
					if (mat[r][d + 1] != p) {
						scelta = -1; // altrimenti assegno -1
						t = false;
					}
				} else {
					scelta = d + 1;
				}
			}
			d = d + 1;
		}
		if (scelta != -2 && scelta != -1) { // se la scelta è diversa da -2, cioè null o da -1 si salva la scelta a d
			d = scelta;
		} else {
			d = -1; // altrimenti assegni -1 a d
		}
		return d; // posizione colonna destra
	}

	/**
	 * Il metodo positionRowLeft, controlla se a partire dalla pedina 1 o 2
	 * presa in cosiderazione, c'è una posizione libera a sinistra e se c'è
	 * bisogna prenderla come scelta e assegnarla a s, sempre che prima non si
	 * trova una posizione occupata da una pedina contraria a quella dalla quale
	 * stiamo partendo con il controllo, in tal caso si assegna a scelta -1.
	 * 
	 * @param r
	 *            numero riga pedina
	 * @param c
	 *            numero colonna pedina
	 * @param p
	 *            valore pedina 1 o 2
	 * @return s variabile che contiene il nnumero della colonna libera a
	 *         sinistra della oedina 1 o 2 presa in cosiderazione
	 */
	private int positionRowLeft(int r, int c, int p) { // calcolo posizione colonna sinistra
		int s = c; // indice riga sinistra
		boolean t = true;
		int scelta = -2; // scelta finale colonna sinistra
		while (s > 0 && scelta == -2) { // controllo prima se la colonna d è maggiore di 0
			if (r > 0) { // se la riga è maggiore di 0 bisogna fare un controllo sulla riga sottostante alla pedina presa in cosiderazione
				if ((mat[r - 1][s - 1] != 0) && t == true) { // se il posto accanto è libero, prendo quel posto come scelta di sinistra
					if (mat[r][s - 1] != 0 && t == true) {
						if (mat[r][s - 1] != p) {
							scelta = -1; // altrimenti assegno -1
							t = false;
						}
					} else {
						scelta = s - 1;
					}
				} else {
					t = false;
					scelta = -1;
				}
			}
			if (r == 0) { // se la riga è uguale a 0 basta il semplice controllo a sinistra
				if (mat[r][s - 1] != 0 && t == true) { // se il posto accanto è libero, prendo quel posto come scelta di sinistra
					if (mat[r][s - 1] != p) {
						scelta = -1; // altrimenti assegno -1
						t = false;
					}
				} else {
					scelta = s - 1;
				}
			}
			s = s - 1;
		}
		if (scelta != -1 && scelta != -2) { // se la scelta è diversa da -2, cioè null o da -1 si salva la scelta a s
			s = scelta;
		} else {
			s = -1; // altrimenti assegni -1 a d
		}
		return s; // posizione colonna sinistra
	}

	/**
	 * Il metodo strategyColumns, partendo dalla posizione della pedina presa in
	 * considerazione 1 o 2, conta quante possibilita si ha per aggiungere una
	 * pedina del proprio valore sopra,controllando se sotto ci sono pedine del
	 * suo stesso valore o colore, e se tale contatore è maggiore o uguale a
	 * tre, (considerando la tabella con i valori standard 6 X 7) si può
	 * aggiungere una pedina sopra, se rs è diverso da -1, cioè se il metodo
	 * positionColumn chiamato ha trovato una posizione libera, salva questo
	 * valore nel vettore poscolonna.
	 * 
	 * @param r
	 *            numero riga pedina
	 * @param c
	 *            numero colonna pedina
	 * @param p
	 *            valore pedina 1 o 2
	 * @return poscolonna che contiene la colonna con un posto libero sopra alla
	 *         pedina 1 o 2 presa in considerazione
	 */
	private int[] strategyColumns(int r, int c, int p) { // mosse attacco su
														// colonna
		int ccs = 0; // contatore colonna sopra
		int ccg = 0; // contatore colonna sotto
		boolean t = true;
		int s = r; // indice riga sopra
		int g = r; // indice riga sotto
		if (s == i - 1) { // se la riga sopra è uguale a 6 si salta l'iterazione altrimenti si controlla se c'è una posto libero
			t = false;
		}
		while (s < i - 1 && t == true) { // se la posizione della riga sopra, c'è un posto libero o una pedia della stesso tipo incrementa il contatore ccs
			if (mat[s + 1][c] == 0 || mat[s + 1][c] == p) {
				ccs = ccs + 1;
			} else {
				t = false;
			}
			s = s + 1;
		}
		int rs = -1;
		if (ccs != 0) { // se il contatore della riga sopra è diverso da 0 si controlla se c'è una posizione libera
			rs = positionColumn(r, c, p); // posizione libera riga sopra
		}
		t = true;
		if (g == 0) { // se la riga presa in cosiderazione è uguale a 0 non si fa un controllo sotto
			t = false;
		}
		while (g > 0 && t == true) { // se la riga è maggiore si controlla se è uguale alla pedina presa inconsiderazione, se è uguale si incrementa il contatore ccg
			if (mat[g - 1][c] == p) {
				ccg = ccg + 1;
			} else {
				t = false;
			}
			g = g - 1;
		}
		int contatore = 0; // posizioni del vettore
		int[] poscolonna = new int[contatore]; // vettore contenente le colonne dell'attacco colonna
		if (ccs + ccg >= 3) { // se il contatore sopra + contatore sotto è maggiore o uguale a 3, cioè è possibile raggiungere un  sopra
			if (rs != -1) { // rs, riga sopra, contiene un posto libero se c'è sopra
				if (contatore > poscolonna.length - 1) { // copia elementi in un vettore provvisorio
					int[] prov = new int[poscolonna.length + 1];
					for (int j = 0; j < poscolonna.length; j++) {
						prov[j] = poscolonna[j];
					}
					poscolonna = prov; // copia gli elementi dal vettore prov provvisorio nel vettore poscolonna
				}
				poscolonna[contatore] = rs; // salva nel vettore poscolonna, la colonna rs
				contatore = contatore + 1;
			}
		}
		return poscolonna; // vettore posizione attacco colonna
	}

	/**
	 * Il metodo positionColumn, controlla se a partire dalla pedina 1 o 2
	 * presa in cosiderazione, c'è una posizione libera sopra e se c'è bisogna
	 * prenderla come scelta e assegnarla a s, sempre che prima non si trova una
	 * posizione occupata da una pedina contraria a quella dalla quale stiamo
	 * partendo con il controllo, in tal caso si assegna a scelta -1.
	 * 
	 * @param r
	 *            numero riga pedina
	 * @param c
	 *            numero colonna pedina
	 * @param p
	 *            valore pedina 1 o 2
	 * @return s che contiene la colonna con un posto libero sopra alla pedina 1
	 *         o 2 presa in considerazione
	 */
	private int positionColumn(int r, int c, int p) { // calcolo posizione sopra
		boolean t = false;
		int s = r; // indice riga sopra
		int scelta = -2; // scelta finale riga sopra
		while (s < i - 1 && scelta == -2 && t == false) { // controllo prima se la riga s è minore di 6
			if (mat[s + 1][c] == 0) { // controllo se la posizione sopra la pedina è libera, se lo è aggiungo a scelta la colonna
				scelta = c;
			} else {
				if (mat[s + 1][c] != p) { // se non è vuota e neanche uguale alla pedina presa in considerazione assegno a scelta -1
					t = true;
					scelta = -1;
				}
			}
			s = s + 1;
		}
		if (scelta != -1 && scelta != -2) { // controllo se il valore scelta è doverso da -1, assegno a s la scelta
			s = scelta;
		} else {
			s = -1; // altrimenti assegno a s -1
		}
		return s; // posizione riga libera sopra
	}

	/**
	 * Il metodo strategyDiagonals, partendo dalla posizione della pedina presa
	 * in considerazione 1 o 2, conta quante possibilita si ha per aggiungere
	 * una pedina del proprio valore prima sulla diaginale destra sopra,poi in
	 * diagonale destra sotto e successivamente in diagonale sinistra sopra e in
	 * diagonale sinistra sotto, se la somma dei contatori per ogni direzione
	 * diagonale è maggiore o uguale a tre, (considerando la tabella con i
	 * valori standard 6 X 7) si può aggiungere una pedina sulla diagonale
	 * superiore destra se dsud è diverso da -1, cioè se il metodo
	 * positionDiagRightUp richiamato ha trovato una posizione libera, una pedina
	 * sulla diagonale inferiore destra se dgd è diverso da -1,cioè se il metodo
	 * positionDiagRightUn richiamato ha trovato una posizione libera, una pedina
	 * sulla diagonale superiore sinistra se dsus è diverso da -1,cioè se il
	 * metodo positionDiagLeftUp richiamato ha trovato una posizione libera, una
	 * pedina sulla diagonale inferiore sinistra se dgs è diverso da -1,cioè se
	 * il metodo positionDiagLeftUn richiamato ha trovato una posizione libera, e
	 * salva questi valori nel vettore poscolonna.
	 * 
	 * @param r
	 *            numero riga pedina
	 * @param c
	 *            numero colonna pedina
	 * @param p
	 *            valore pedina 1 o 2
	 * @return poscolonna che contiene la colonna con un posto libero in
	 *         diagonale alla pedina 1 o 2 presa in considerazione //
	 */
	private int[] strategyDiagonals(int r, int c, int p) { // mosse attacco diagonale
		int cdds = 0; // contatore diagonale destra sopra
		int cddg = 0; // contatore diagonale destra sotto
		int cdss = 0; // contatore diagonale sinistra sotto
		int cdsg = 0; // contatore diagonale sinistra sotto
		boolean t = true;
		int d = c; // colonna verso destra
		int s = c; // colonna verso sinistra
		int su = r; // riga sopra
		int g = r; // riga sotto o giù
		if (d == j - 1) { 
			t = false;
		}
		while (d < j - 1 && su < i - 1 && t == true) { // diagonale destra sopra
			if (mat[su][d + 1] != 0) {
				if (mat[su + 1][d + 1] == 0 || mat[su + 1][d + 1] == p) {
					cdds = cdds + 1;
				} else {
					t = false;
				}
			} else {
				t = false;
			}
			d = d + 1;
			su = su + 1;
		}
		int dsud = -1;
		if (cdds != 0) {
			dsud = positionDiagRightUp(r, c, p); // posizione diagonale destra
		}
		d = c;
		t = true;
		if (g < 1) {
			t = false;
		}
		while (d < j - 1 && g > 0 && t == true) { // diagonale destra sotto
			if (mat[g - 1][d + 1] == 0 || mat[g - 1][d + 1] == p) {
				cddg = cddg + 1;
			} else {
				t = false;
			}
			d = d + 1;
			g = g - 1;
		}
		int dgd = -1;
		if (cddg != 0) {
			dgd = positionDiagRightUn(r, c, p); // posizione diagonale destra sotto
		}
		t = true;
		su = r; // inizializza di nuovo le righe
		g = r;
		if (s == 0) { // diagonale sinistra
			t = false;
		}
		while (s > 0 && su < i - 1 && t == true) { // diagonale sinistra sopra
			if (mat[su][s - 1] != 0) {
				if (mat[su + 1][s - 1] == 0 || mat[su + 1][s - 1] == p) {
					cdss = cdss + 1;
				} else {
					t = false;
				}
			} else {
				t = false;
			}
			s = s - 1;
			su = su + 1;
		}

		int dsus = -1;
		if (cdss != 0) {
			dsus = positionDiagLeftUp(r, c, p); // posizione diagonale sinistra sopra
		}
		t = true;
		s = c;
		if (g < 1) {
			t = false;
		}
		while (s > 0 && g > 0 && t == true) { // diagonale sinistra sotto
			if (mat[g - 1][s - 1] == 0 || mat[g - 1][s - 1] == p) {
				cdsg = cdsg + 1;
			} else {
				t = false;
			}
			s = s - 1;
			g = g - 1;
		}

		int dgs = -1;
		if (cdsg != 0) {
			dgs = positionDiagLeftUn(r, c, p); // posizione diagonale sinistra sotto
		}
		int contatore = 0;
		int[] poscolonna = new int[contatore];
		if (cdds + cddg + cdss + cdsg >= 3) {
			if (dsud != -1) {
				if (contatore > poscolonna.length - 1) {
					int[] prov = new int[poscolonna.length + 1];
					for (int j = 0; j < poscolonna.length; j++) {
						prov[j] = poscolonna[j];
					}
					poscolonna = prov;
				}
				poscolonna[contatore] = dsud;
				contatore = contatore + 1;
			}
			if (dgd != -1) {
				if (contatore > poscolonna.length - 1) {
					int[] prov = new int[poscolonna.length + 1];
					for (int j = 0; j < poscolonna.length; j++) {
						prov[j] = poscolonna[j];
					}
					poscolonna = prov;
				}
				poscolonna[contatore] = dgd;
				contatore = contatore + 1;
			}
			if (dsus != -1) {
				if (contatore > poscolonna.length - 1) {
					int[] prov = new int[poscolonna.length + 1];
					for (int j = 0; j < poscolonna.length; j++) {
						prov[j] = poscolonna[j];
					}
					poscolonna = prov;
				}
				poscolonna[contatore] = dsus;
				contatore = contatore + 1;
			}
			if (dgs != -1) {
				if (contatore > poscolonna.length - 1) {
					int[] prov = new int[poscolonna.length + 1];
					for (int j = 0; j < poscolonna.length; j++) {
						prov[j] = poscolonna[j];
					}
					poscolonna = prov;
				}
				poscolonna[contatore] = dgs;
				contatore = contatore + 1;
			}
		}
		return poscolonna; // posizione/i diagonale
	}

	/**
	 * Il metodo positionDiagRightUp, controlla se a partire dalla pedina 1 o 2
	 * presa in cosiderazione, c'è una posizione libera nella diagonale
	 * superiore destra e se c'è bisogna prenderla come scelta e assegnarla a d,
	 * sempre che prima non si trova una posizione occupata da una pedina
	 * contraria a quella dalla quale stiamo partendo con il controllo, in tal
	 * caso si assegna a scelta -1.
	 * 
	 * @param r
	 *            numero riga pedina
	 * @param c
	 *            numero colonna pedina
	 * @param p
	 *            valore pedina 1 o 2
	 * @return d che contiene la colonna con un posto libero nella diagonale
	 *         superiore destra alla pedina 1 o 2 presa in considerazione
	 */
	private int positionDiagRightUp(int r, int c, int p) { // calcolo posizione diagonale destra sopra
		int su = r;
		int d = c;
		int scelta = -2;
		while (d < j - 1 && su < i - 1 && scelta == -2) {
			if (mat[su][d + 1] != 0) {
				if (mat[su + 1][d + 1] != p) {
					if (mat[su + 1][d + 1] == 0) {
						scelta = d + 1;
					} else {
						scelta = -1;
					}
				}
			} else {
				scelta = -1;
			}
			d = d + 1;
			su = su + 1;
		}
		if (scelta != -1 && scelta != -2) {
			d = scelta;
		} else {
			d = -1;
		}
		return d; // posizione diagonale destra sopra
	}

	/**
	 * Il metodo positionDiagRightUn, controlla se a partire dalla pedina 1 o 2
	 * presa in cosiderazione, c'è una posizione libera nella diagonale
	 * inferiore destra e se c'è bisogna prenderla come scelta e assegnarla a d,
	 * sempre che prima non si trova una posizione occupata da una pedina
	 * contraria a quella dalla quale stiamo partendo con il controllo, in tal
	 * caso si assegna a scelta -1.
	 * 
	 * @param r
	 *            numero riga pedina
	 * @param c
	 *            numero colonna pedina
	 * @param p
	 *            valore pedina 1 o 2
	 * @return d che contiene la colonna con un posto libero nella diagonale
	 *         inferiore destra alla pedina 1 o 2 presa in considerazione
	 */
	private int positionDiagRightUn(int r, int c, int p) { // calcolo posizione diagonale destra sotto
		int g = r;
		int d = c;
		int scelta = -2;
		if (g > 0) {
			while (d < j - 1 && g > 0 && scelta == -2) {
				if (mat[g - 1][d + 1] != p) {
					if (mat[g - 1][d + 1] == 0) {
						scelta = d + 1;
					} else {
						scelta = -1;
					}
				}
				d = d + 1;
				g = g - 1;
			}
		}
		if (scelta != -1 && scelta != -2) {
			d = scelta;
		} else {
			d = -1;
		}
		return d; // posizione diagonale destra sotto
	}

	/**
	 * Il metodo positionDiagLeftUp, controlla se a partire dalla pedina 1 o 2
	 * presa in cosiderazione, c'è una posizione libera nella diagonale
	 * superiore sinistra e se c'è bisogna prenderla come scelta e assegnarla a
	 * s, sempre che prima non si trova una posizione occupata da una pedina
	 * contraria a quella dalla quale stiamo partendo con il controllo, in tal
	 * caso si assegna a scelta -1.
	 * 
	 * @param r
	 *            numero riga pedin a
	 * @param c
	 *            numero colonna pedina
	 * @param p
	 *            valore pedina 1 o 2
	 * @return s che contiene la colonna con un posto libero nella diagonale
	 *         superiore sinistra alla pedina 1 o 2 presa in considerazione
	 */
	private int positionDiagLeftUp(int r, int c, int p) { // calcolo posizione diagonale/ sinistra sopra
		int su = r;
		int s = c;
		int scelta = -2;
		while (s > 0 && su < i - 1 && scelta == -2) {
			if (mat[su][s - 1] != 0) {				
				if (mat[su + 1][s - 1] != p) {
					if (mat[su + 1][s - 1] == 0) {
						scelta = s - 1;
					} else {
						scelta = -1;
					}
				}
			} else {
				scelta = -1;
			}
			s = s - 1;
			su = su + 1;
		}
		if (scelta != -1 && scelta != -2) {
			s = scelta;
		} else {
			s = -1;
		}
		return s; // posizione diagonale sinistra sopra
	}

	/**
	 * Il metodo positionDiagLeftUn, controlla se a partire dalla pedina 1 o 2
	 * presa in cosiderazione, c'è una posizione libera nella diagonale
	 * inferiore sinistra e se c'è bisogna prenderla come scelta e assegnarla a
	 * s, sempre che prima non si trova una posizione occupata da una pedina
	 * contraria a quella dalla quale stiamo partendo con il controllo, in tal
	 * caso si assegna a scelta -1.
	 * 
	 * @param r
	 *            numero riga pedina
	 * @param c
	 *            numero colonna pedina
	 * @param p
	 *            valore pedina 1 o 2
	 * @return s che contiene la colonna con un posto libero nella diagonale
	 *         inferiore sinistra alla pedina 1 o 2 presa in considerazione
	 */
	private int positionDiagLeftUn(int r, int c, int p) { // calcolo posizione diagonale sinistra sotto
		int g = r;
		int s = c;
		int scelta = -2;
		if (g > 0) {
			while (s > 0 && g > 0 && scelta == -2) {
				if (mat[g - 1][s - 1] != p) {
					if (mat[g - 1][s - 1] == 0) {
						scelta = s - 1;
					} else {
						scelta = -1;
					}
				}
				s = s - 1;
				g = g - 1;
			}
		}
		if (scelta != -1 && scelta != -2) {
			s = scelta;
		} else {
			s = -1;
		}
		return s; // posizione diagonale sinistra sotto
	}

	/**
	 * Il metodo tellMove, riceve un intero c che corrisponde alla colonna nella
	 * quale l'avversario ha inserito la pedina, e aggiorna la matrice per poi
	 * ripartire con il metodo move.
	 * 
	 * @param c
	 *            colonna mossa avversaria
	 */
	public void tellMove(int c) {
		mat[col[c]][c] = 2;
		col[c] = col[c] + 1;
	}

}
