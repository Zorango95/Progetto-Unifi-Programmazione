package gj.forza4.player;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * La classe crea l'interfaccia di un giocatore di forsa 4 con 6 righe e 7
 * colonnne. Fa uso di un pannello per la griglia con un'immagine in formato png
 * che supporta la trasparenza e sette pannelli sottostanti (uno per ogni
 * colonna) dove vengono disegnati i dischi.
 * 
 * @author rragami
 *
 */

@SuppressWarnings("serial")
public class NewHumanPlayer extends AbstractHumanPlayer {

	private int nc;
	private int nr;
	private int[][] tabella;
	private int[] altezzaCol;
	private boolean attesa = false;

	private BufferedImage griglia;
	private BufferedImage disc1;
	private BufferedImage disc2;

	private int dimClonna = 85;

	Color colore = new Color(53, 66, 76);
	PannelloSup panSup;
	Pannello pan1;
	Pannello pan2;
	Pannello pan3;
	Pannello pan4;
	Pannello pan5;
	Pannello pan6;
	Pannello pan7;

	public NewHumanPlayer() {
		setTitle("Forza 4");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(604, 636);
		setLocationRelativeTo(null);
		getContentPane().setBackground(colore);
		getContentPane().setLayout(null);
		try {
			griglia = ImageIO.read(new File("tabella.jpg"));
			disc1 = ImageIO.read(new File("pedinarossa.jpg"));
			disc2 = ImageIO.read(new File("pedinagialla.jpg"));
		} catch (IOException ex) {
			System.out.println("Immagine non trovata");
			System.exit(0);
		}

		pan1 = new Pannello(0, 0);
		pan2 = new Pannello(dimClonna, 1);
		pan3 = new Pannello(dimClonna * 2, 2);
		pan4 = new Pannello(dimClonna * 3, 3);
		pan5 = new Pannello(dimClonna * 4, 4);
		pan6 = new Pannello(dimClonna * 5, 5);
		pan7 = new Pannello(dimClonna * 6, 6);

		panSup = new PannelloSup();
		getContentPane().add(panSup);
		getContentPane().add(pan1);
		getContentPane().add(pan2);
		getContentPane().add(pan3);
		getContentPane().add(pan4);
		getContentPane().add(pan5);
		getContentPane().add(pan6);
		getContentPane().add(pan7);
	}

	@Override
	public void start(int arg0, int arg1) {
		nr = arg0;
		nc = arg1;
		tabella = new int[nc][nr];
		altezzaCol = new int[nc];
		inizializzaVettori();

		setVisible(true);
	}

	@Override
	public void tellMove(int arg0) {
		if (arg0 == 0) {
			pan1.avviaTimer(2);
		} else if (arg0 == 1) {
			pan2.avviaTimer(2);
		} else if (arg0 == 2) {
			pan3.avviaTimer(2);
		} else if (arg0 == 3) {
			pan4.avviaTimer(2);
		} else if (arg0 == 4) {
			pan5.avviaTimer(2);
		} else if (arg0 == 5) {
			pan6.avviaTimer(2);
		} else if (arg0 == 6) {
			pan7.avviaTimer(2);
		}
	}

	@Override
	protected void humanPlayerMove(int arg0) {
		aggiungiMossaInTabella(arg0, 1);
		if (arg0 == 0) {
			pan1.repaint();
		} else if (arg0 == 1) {
			pan2.repaint();
		} else if (arg0 == 2) {
			pan3.repaint();
		} else if (arg0 == 3) {
			pan4.repaint();
		} else if (arg0 == 4) {
			pan5.repaint();
		} else if (arg0 == 5) {
			pan6.repaint();
		} else if (arg0 == 6) {
			pan7.repaint();
		}
	}

	// -----------------------------------

	/**
	 * Il mettodo imposta a zero i valori dei due array utilizzati per
	 * memorizzare le mosse dei due giocatori
	 */
	private void inizializzaVettori() {
		for (int r = 0; r < tabella[0].length; r++) {
			for (int c = 0; c < tabella.length; c++) {
				tabella[c][r] = 0;
			}
		}
		for (int i = 0; i < altezzaCol.length; i++) {
			altezzaCol[i] = 0;
		}
	}

	/**
	 * Aggiorna l'array tabella aggiungendo un disco.
	 * 
	 * @param c
	 *            indice della colonna che deve essere aggiornata
	 * @param n
	 *            giocatore che effettua la mossa, 1 per la propria mossa e 2
	 *            per la mossa dell'avversario
	 */
	private void aggiungiMossaInTabella(int c, int n) {
		tabella[c][altezzaCol[c]] = n;
		aumentaAltezzaColonna(c);
	}

	/**
	 * Aggiorna l'array altezzaCol il quale viene utilizzato per indicare quanti
	 * dischi contiene in ogni momento ciascuna colonna.
	 * 
	 * @param c
	 *            indice della colonna per la quale deve essere incrementata di
	 *            uno il numero dei dischi presenti.
	 */
	public void aumentaAltezzaColonna(int c) {
		altezzaCol[c] = altezzaCol[c] + 1;
	}

	/**
	 * La classe crea un pannello trasparente delle dimensioni di una colonna
	 * dove verranno disegnati i dischi
	 * 
	 * @author rragami
	 *
	 */
	private class Pannello extends JPanel implements ActionListener {
		Timer timer;
		int y = 20;
		int giocatore = -1;
		private boolean discSospeso;
		int col;

		/**
		 * Il costruttore permette di specificare dove piazzare il pannello e a
		 * quale colonna associaro
		 * 
		 * @param x
		 *            posizione della coordinata x dove verra' aggiunto il
		 *            pannello
		 * @param c
		 *            indice della colonna
		 */
		public Pannello(int x, int c) {
			setBounds(x, 0, dimClonna + 6, 636);
			col = c;
			discSospeso = false;
			addMouseListener(new AzioniMouse());
			setOpaque(false);
		}

		/**
		 * Il metodo avvia un il timer
		 * 
		 * @param g
		 *            giocatore per il quale disegnare il disco
		 */
		public void avviaTimer(int g) {
			attesa = true;
			giocatore = g;
			timer = new Timer(5, this);
			timer.start();
		}

		/**
		 * Il metodo modifica il valore della variabile booleana discoSopseso
		 * per non permettere all'utente di cliccare una seconda volta su una
		 * colonna prima che il disco aabbia raggiunto la sua posizione
		 * 
		 * @param ds
		 *            valore booleano per indicare se un disco ha raggiunto la
		 *            propria posizione oppure se e' ancora in caduta
		 */
		public void setDiscSospeso(boolean ds) {
			discSospeso = ds;
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (y > 20) {
				if (giocatore == 1) {
					g.drawImage(disc1, 11, y, null);
				} else {
					g.drawImage(disc2, 11, y, null);
				}
			} else if (discSospeso == true && altezzaCol[col] < nr) {
				g.setColor(Color.GRAY);
				g.fillRect(5, 70, 85, 500);
				g.drawImage(disc1, 12, 20, null);
			}

			for (int r = 0; r < tabella[0].length; r++) {
				if (tabella[col][r] == 1) {
					g.drawImage(disc1, 12, 492 - (r * 83), null);
				} else if (tabella[col][r] == 2) {
					g.drawImage(disc2, 12, 492 - (r * 83), null);
				}
			}

		}

		/**
		 * Il metodo viene invocato dal timer e ridisegna il pannello ogni volta
		 * aumentanto y di 5 pixel fino a quando il disco non araggiunge la
		 * propria posizione indicata dalla variabile posFinale.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			this.repaint();
			y = y + 5;
			int posFinale = 470 - (altezzaCol[col] * dimClonna);
			if (y > posFinale) {
				y = 5;
				if (giocatore == 1) {
					setMove(col);
				} else if (giocatore == 2) {
					aggiungiMossaInTabella(col, 2);
				}
				giocatore = -1;
				attesa = false;
				timer.stop();
			}
		}
	}

	/**
	 * La classe crea un pannello che si posiziona sopra a tutti gli altri e che
	 * viene caricato e disegnato soltatno una vaolta quando il HumanPlayer
	 * viene istanziato. L'immagine e' in formato png con supporto della
	 * trasparenza.
	 * 
	 * @author rragami
	 *
	 */
	public class PannelloSup extends JPanel {
		public PannelloSup() {
			setBounds(0, 0, 604, 636);
			setPreferredSize(new Dimension(600, 700));
			setOpaque(false);
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(griglia, 0, 0, null);
		}
	}

	private class AzioniMouse implements MouseListener {
		@Override
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (!attesa) {
				if (e.getComponent().equals(pan1) && altezzaCol[0] < nr) {
					pan1.avviaTimer(1);
				} else if (e.getComponent().equals(pan2)) {
					pan2.avviaTimer(1);
				} else if (e.getComponent().equals(pan3)) {
					pan3.avviaTimer(1);
				} else if (e.getComponent().equals(pan4)) {
					pan4.avviaTimer(1);
				} else if (e.getComponent().equals(pan5)) {
					pan5.avviaTimer(1);
				} else if (e.getComponent().equals(pan6)) {
					pan6.avviaTimer(1);
				} else if (e.getComponent().equals(pan7)) {
					pan7.avviaTimer(1);
				}
			}
		}

		@Override
		public void mousePressed(java.awt.event.MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseReleased(java.awt.event.MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseEntered(java.awt.event.MouseEvent e) {
			if (!attesa) {
				if (e.getComponent().equals(pan1) && altezzaCol[0] < nr) {
					pan1.setDiscSospeso(true);
					pan1.repaint();
				} else if (e.getComponent().equals(pan2)) {
					pan2.setDiscSospeso(true);
					pan2.repaint();
				} else if (e.getComponent().equals(pan3)) {
					pan3.setDiscSospeso(true);
					pan3.repaint();
				} else if (e.getComponent().equals(pan4)) {
					pan4.setDiscSospeso(true);
					pan4.repaint();
				} else if (e.getComponent().equals(pan5)) {
					pan5.setDiscSospeso(true);
					pan5.repaint();
				} else if (e.getComponent().equals(pan6)) {
					pan6.setDiscSospeso(true);
					pan6.repaint();
				} else if (e.getComponent().equals(pan7)) {
					pan7.setDiscSospeso(true);
					pan7.repaint();
				}
			}

		}

		@Override
		public void mouseExited(java.awt.event.MouseEvent e) {
			if (e.getComponent().equals(pan1)) {
				pan1.setDiscSospeso(false);
				pan1.repaint();
			} else if (e.getComponent().equals(pan2)) {
				pan2.setDiscSospeso(false);
				pan2.repaint();
			} else if (e.getComponent().equals(pan3)) {
				pan3.setDiscSospeso(false);
				pan3.repaint();
			} else if (e.getComponent().equals(pan4)) {
				pan4.setDiscSospeso(false);
				pan4.repaint();
			} else if (e.getComponent().equals(pan5)) {
				pan5.setDiscSospeso(false);
				pan5.repaint();
			} else if (e.getComponent().equals(pan6)) {
				pan6.setDiscSospeso(false);
				pan6.repaint();
			} else if (e.getComponent().equals(pan7)) {
				pan7.setDiscSospeso(false);
				pan7.repaint();
			}

		}
	}

}
