
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.ImageIcon;

public class Team___Dodge {
	
   public static void main(String[] args) {
      Gameplay GST = new Gameplay();
      GST.Sound("bgm/bgm2.wav", true); //배경음악 재생
   }
}

class Gameplay extends JFrame implements Runnable, KeyListener {
   boolean keyUp = false; 
   boolean keyDown = false;
   boolean keyLeft = false;
   boolean keyRight = false;
   boolean space = false;
   boolean P_pause = false;
   boolean enter = false;
   boolean r = false;
   boolean b = false;
   private ArrayList<Asteroid> A_array = new ArrayList<Asteroid>(); // 방해물 오브젝트를 여러개 저장히기 위한 배열
   private Asteroid pr; // 방해물 클래스의 접근자
   private Image C_img = new ImageIcon("img/Mk2.png").getImage(); //주인공 이미지
   private Image bg = new ImageIcon("img/gamebg.png").getImage(); // 배경
   private Image profimg = new ImageIcon("img/bullet.png").getImage(); //방해물 이미지
   private Image Firstview = new ImageIcon("img/main2.png").getImage(); //시작전 이미지
   private Image icon = new ImageIcon("img/Mk4.png").getImage();
   private ImageIcon icon2 = new ImageIcon("img/Mk4.png");
   private Image buffimg = null; // 버퍼이미지
   private Graphics gc;
   private int c_x = 250, c_y = 250, c_w = 25, c_h = 25, A_w = 20, A_h = 20; // 캐릭터의 시작 위치, 그리고 앞으로의 좌표를 받아오기 위한 변수 설정된건 중앙에서 시작하게되어있슴
   static int cnt = 0; // 쓰레드의 루프를 세는 변수, 각종 변수를 통제(시간 등)하기 위해 사용된다
   private int life = 0; // 목숨카운트
   private int distance = 0; // 두 지점 거리 측정용
   private int mode = 0;
   private int A_Cycle = 0;
   private int selectmode = 1;
   private int nanedo = 0;
   private int chun = 1000;
   private int temp = 0;
   boolean pause = false;
   boolean A_obj = false;
   private static String selectstr = null;
   private String[] selections = {"난이도:보통", "난이도:어려움", "난이도:HELL"};
   
   public Gameplay() {
	  cnt = 0;

	  setTitle("너에게 닷지");
      setSize(500, 500);
      setIconImage(icon);
      modeS();
      start(); // 쓰레드의 루프를 시작하기 위한 메써드
      setResizable(false); // 사이즈를 조절할 수 없게 만듬
      setVisible(true); // 프레임을 보이게 만듬
      this.addKeyListener(this); // 키리스너를 추가하여 방향키 정보를 받아올 수 있게 한다.
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      // Dimension: 특정사각형 영역을 관리하기 편한 클래스
      Dimension frameSize = this.getSize(); //프레임크기구하기
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //모니터크구하기
      this.setLocation((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
      //(모니터화면 가로 - 프레임화면 가로) / 2,
      //(모니터화면 세로 - 프레임화면 세로) / 2 이렇게 설정한다.
   }
   
   public void modeS() {
   	  	selectstr = (String) JOptionPane.showInputDialog(null, "너에게 닷지 실행전에" + "\n" + "게임 난이도를 선택해 주세요.", "너에게 닷지",
   	  				JOptionPane.PLAIN_MESSAGE, icon2, selections, selections[0]);
   	  	if(selectstr == selections[0])
   	  		selectmode = 1;
   	  	else if(selectstr == selections[1])
   	  		selectmode = 2;
   	  	else if(selectstr == selections[2])
   	  		selectmode = 3;
   	  	else
   	  		System.exit(0);
   }
   
   public void Sound(String file, boolean Loop){ //배경음악재생 및 효과음 담당
	 //사운드재생용메소드
	 //메인 클래스에 추가로 메소드를 하나 더 만들었습니다.
	 //사운드파일을받아들여해당사운드를재생시킨다.
	 Clip clip;
	 try {
	 AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
	 clip = AudioSystem.getClip();
	 clip.open(ais);
	 clip.start();
	 if ( Loop) clip.loop(-1);
	 //Loop 값이true면 사운드재생을무한반복시킵니다.
	 //false면 한번만재생시킵니다.
	 } catch (Exception e) {
		 e.printStackTrace();
	 	}
	 }

   public void start() {
      Thread th = new Thread(this); // 쓰레드 를 정의
      th.start(); // 쓰레드의 루프를 시작시킨다
   }

   public void run() {
      while (life == 0) { // life가 0이하로 떨어지면 루프가 끝난다
    	  try {
            mode_pause();
            arrowkey(); // 받은 키에 따른 캐릭터의 이동을 통제
            repaint(); // 리페인트함수(그림을 그때그때 새로기리기위함)
            Thread.sleep(20); // 20밀리세컨드당 한번의 루프가 돌아간다
            if (pause == false) {
               if (mode != 0) {
                  if (r == true) {
                     reset();// r을 누를시 리셋됩니다.
                  }
                  A_P_move(); // 방해물 추가/움직이게 함
                  cnt++; // 루프가 돌아간 횟수
                  if(chun <= cnt && cnt <= chun+10){c_w += 1; c_h += 1; nanedo = 3;} //20초가되면 주인공크기가 10더 커지고 10초간지속
                  if(chun+500 <= cnt && cnt <= chun+510){c_w -= 1; c_h -= 1; nanedo = 0;}
                  if(chun+511 == cnt){chun+=1500;} //20초 50초 80초 110초 단위로 작용
               }
            }
         } catch (Exception e) {
        	 e.printStackTrace();
         }
      }
   }

   public void paint(Graphics g) { // 각종 이미지를 그리기위한메서드를 실행시킨다
      buffimg = createImage(500, 500); // 버퍼이미지를 그린다 (떠블버퍼링을 사용하여 화면의 깜빡임을 없앤다)
      gc = buffimg.getGraphics(); // 버퍼이미지에 대한 그래픽 객채를 얻어온다.
      drawimages(g);
   }

   public void drawimages(Graphics g) {
      if (mode == 0) {
         setBackground(Color.BLACK);
         gc.drawImage(Firstview, 0,10, this);
         g.drawImage(buffimg, 0, 0, this);
      } 
      else {
    	 gc.drawImage(bg, 0, 0, this);// 배경의 그림을 그린다
         profDrawImg(); // 방해물의 그림을 그린다
         stdDrawImg(); // 비행기의 그림과 점수을 그린다
         g.drawImage(buffimg, 0, 0, this); // 버퍼이미지를 그린다. 0,0으로 좌표
      }

   }

   public void stdDrawImg() {
      gc.setColor(Color.white);
      gc.setFont(new Font("Default", Font.BOLD, 20)); // 폰트의모양과크기를정나다
      gc.drawString("점수(초당 1점): " + Integer.toString(cnt / 50) + "점", 10, 50);
      gc.drawString("운석수: " + Integer.toString(A_array.size()) + "개", 380, 50);// 루프속도의1/40만큼시간이흘러감
      if (pause == true) {
         gc.drawString("PAUSE", 220, 250); //일시정지 상태에서 PAUSE를 나타냅니다
      }
      gc.drawImage(C_img, c_x, c_y, c_w, c_h, this);// 계속다시그리므로 움직이는것처럼 보임)
   }
   
   public void profDrawImg() {
      for (int i = 0; i < A_array.size(); ++i) {
         pr = (Asteroid) (A_array.get(i));
         gc.drawImage(profimg, pr.At_Pos[0], pr.At_Pos[1], A_w, A_h, this);
      }// 추가된 방해물의 수만큼 돌아다니는 방해물의 그림을 추가한다.
   }
   
   public static int returntoP() {
	   return (cnt/50);
   }

   public void A_P_move() {
      for (int i = 0; i < A_array.size(); ++i) {
         pr = (Asteroid) (A_array.get(i)); // 방해물을 추가시킨다.
         pr.move(); // 방해물의 움직임을 통제하는 메서드를 불러온다
         double dist = Math.sqrt(Math.pow(Math.abs(c_x - pr.At_Pos[0]), 2) + Math.pow(Math.abs(c_y - pr.At_Pos[1]), 2)); // 방해물과 주인공의 거리를 재는 알고리즘
         if (dist < 15 + nanedo) { // 거리가줄어들면게임오버
            if (A_obj == false) {
               life--;
               Sound("bgm/lose.wav", false);
               new Gameover();
               dispose(); // 게임프레임은 닫는다
            }
         }
      }
      if((A_array.size() == 40 && temp == 0) || (A_array.size() == 70 && temp == 0))
    	  temp = cnt / 50; //운석수가 40개와 70개가 됐을 때 10초동안 생산정지 모든모드적용
      
      if(cnt / 50 < temp+10 && temp != 0); //10초후작동
      else {temp = 0;
      if ((cnt) % A_Cycle == 0 ) {
         int[] r = GenerateXNY(); // 좌표를 랜덤으로 받아온다
         pr = new Asteroid(r[0], r[1]); // 받아온 좌표에 cnt/100의 시간이 지날때마다
                                    // 방해물을추가시킴
         A_array.add(pr);
      	}
      }
   }

   int[] GenerateXNY() { // 좌표를 랜덤으로 불러오는 메써드
      Random rand = new Random();
      int[] res = new int[2];
      int C = (rand.nextInt(3)+1);
      int x_rand = 0, y_rand = 0;
      
      if(C == 1){
      x_rand = (rand.nextInt(200)-200);
      y_rand = ((rand.nextInt(900)) - 200); 
      }
      else if(C == 2){
      x_rand = ((rand.nextInt(900)) - 200);
      y_rand = (rand.nextInt(200)-200);
      }
      else if(C == 3){
      x_rand = ((rand.nextInt(200)) + 500);
      y_rand = ((rand.nextInt(900)) - 200); 
      }
      else if(C == 4){
      x_rand = ((rand.nextInt(900)) - 200);
      y_rand = ((rand.nextInt(200)) + 500);
      }
      
         res[0] = x_rand;
         res[1] = y_rand;
         return res; 
   }

   public void reset() { // 게임을 모두 초기상태로 돌린다
      A_array.clear();
      distance = 0; mode = 0; cnt = 0; //들여쓰기임
      c_x = 250; c_y = 250;
   }

   public void mode_pause() {
	  if (b == true)
		Firstview = new ImageIcon("img/bg2.png").getImage();
	  else
		Firstview = new ImageIcon("img/main2.png").getImage();
	   
      if (mode == 0) {
         if (enter == true) {
            mode = selectmode; //엔터를 누르면 해당모드를 적용시킨 후 게임이 시작된다
         }
         if (mode == 1) { // 모드1
            A_Cycle = 50; // 모드1에서는 방해물생성 주기가 김
         } else if (mode == 2) {
            A_Cycle = 30; 
         } else if (mode == 3) {
            A_Cycle = 15; //방해물속도주기 낮을수록 빨라짐
         }
      }
      if (P_pause == true) { // p를 누르면 게임이 멈춥니다 스탑 기능
         pause = !pause;
         P_pause = false;
      }
   }

   public void arrowkey() { // 캐릭터의 이동속도와 방향키에 따른 이동방향을 결정하고, 캐릭터를 화면
                        // 밖으로못빠져나가가게합니다
      if (mode != 0) {
         if (keyUp == true && pause == false) {
            if (c_y > 25) {
               if (space == false) {c_y -= 4;} 
               else {c_y -= 7;}
            }
         }
         if (keyDown == true && pause == false) {
            if (c_y + c_h < 500) {
               if (space == false) {c_y += 4;}
               else {c_y += 7;}
            }
         }
         if (keyLeft == true && pause == false) {
            if (c_x > 0) {
               if (space == false) {c_x -= 4;} 
               else {c_x -= 7;}
            }
         }
         if (keyRight == true && pause == false) {
            if (c_x + c_w < 500) {
               if (space == false) {c_x += 4;} 
               else {c_x += 7;}
            }
         }
         if (enter == true) {
            mode = selectmode;
         }
      }
   }

   public void keyPressed(KeyEvent e) { // 방향키를 눌렀을때 눌렀다는 신호를 받아온다( 키입력을
                                 // true로만든다)

      if (e.getKeyCode() == KeyEvent.VK_LEFT) {
         keyLeft = true;
      } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
         keyRight = true;
      } else if (e.getKeyCode() == KeyEvent.VK_UP) {
         keyUp = true;
      } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
         keyDown = true;
      } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
         space = true;
      } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
         P_pause = true;
      } else if (e.getKeyCode() == KeyEvent.VK_R) {
         r = true;
      } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
         enter = true;
      } else if (e.getKeyCode() == KeyEvent.VK_B) {
          b = true;
      }
      
   }

   public void keyReleased(KeyEvent e) { // 키를 떼었을때 키입력을 false로 만든다

      if (e.getKeyCode() == KeyEvent.VK_LEFT) {
         keyLeft = false;
      } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
         keyRight = false;
      } else if (e.getKeyCode() == KeyEvent.VK_UP) {
         keyUp = false;
      } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
         keyDown = false;
      } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
         space = false;
      } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
         P_pause = false;
      } else if (e.getKeyCode() == KeyEvent.VK_R) {
         r = false;
      } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
         enter = false;
      } else if (e.getKeyCode() == KeyEvent.VK_B) {
          b = false;
      }
   }

   public void keyTyped(KeyEvent e) {}

}

class Asteroid { // 방해물클래스
   int At_Pos[] = new int[2]; // 방해물의 위치를 랜덤으로 받아오기 위한 변수
   private int At_Speed; // 방해물의 이동속도를 조절하기 위한 변수
   private int dst[] = new int[2]; // 방해물의 목적지를 정하기 위한 변수

   Asteroid(int x, int y) {
      At_Pos[0] = x;
      At_Pos[1] = y;
      dst = GenerateXNY(); // 목적지를 랜덤으로 받아온다
      Random rand = new Random();
      At_Speed = (rand.nextInt(2) + 1); // 1과 3사이의 속도를 받아온다
   }

   int[] resetDst() { // 좌표를 다시 정해서 리턴시키는 메서드
      int res[] = new int[2];
      res = GenerateXNY();
      
      return res;
   }

   int[] GenerateXNY() { // 좌표를 랜덤으로 리턴시키는 메서드
      Random rand = new Random();
      int x_rand = (rand.nextInt(900)) - 200;
      int y_rand = (rand.nextInt(900)) - 200;
      int[] res = new int[2];
      res[0] = x_rand;
      res[1] = y_rand;
      
      return res;
   }

   public void move() { // 방해물의 움직임을 통제하기 위한 메서드
      if (At_Pos[0] >= dst[0]) {
         At_Pos[0] = At_Pos[0] - At_Speed;
      }
      if (At_Pos[0] <= dst[0]) {
         At_Pos[0] = At_Pos[0] + At_Speed;
      }

      if (At_Pos[1] >= dst[1]) {
         At_Pos[1] = At_Pos[1] - At_Speed;
      }
      if (At_Pos[1] <= dst[1]) {
         At_Pos[1] = At_Pos[1] + At_Speed;
      } // 방해물의 현재 위치가 방해물전용 지역에 있고 도착지점위치를 파악하여 어느 방향으로 어떤속도로 날아갈지 결정 
      
      if (Math.abs(At_Pos[0] - dst[0]) <= At_Speed * 2) {
         dst = resetDst();
      }
      
      if (Math.abs(At_Pos[1] - dst[1]) <= At_Speed * 2) {
         dst = resetDst();
      }
      // 방해물이 목적지 근처에 도착하였을 경우 목적지를 재설정한다
   }
}

class Gameover extends JFrame implements ActionListener /*,KeyListener*/ {
	  
	JButton restart;
	private int result_P = Gameplay.returntoP();
	private int stoporgo;
	private ImageIcon icon = new ImageIcon("img/Mk4.png");
	private String str;
	  
   	  public Gameover() {
   	  evaluation();
   		  
   	  stoporgo = JOptionPane.showConfirmDialog(null, "최종점수는" + result_P + "점 입니다." + "\n" + str + "\n" + "다시 할꺼임?^?", "너에게 닷지", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
   	  exitorrestart();
   	  
   	  restart = new JButton();
   	  restart.setText("재시작");
   	  restart.addActionListener(this);
   	  
   	  setTitle("너에게 닷지");
      setResizable(false);
      setLayout(null);
      setSize(500, 500);
      setVisible(true);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      
      restart.setBounds(0, 0, 495, 475);
      restart.doClick();
      
      add(restart);
   }
   
   public void evaluation() {
	   if(0 < result_P && result_P <= 20)
		   str = result_P + "점? 완전 못하시네~";
	   else if(20 < result_P && result_P <= 40)
		   str = result_P + "점이라.. 그래도 좀 하시네~";
	   else if(40 < result_P && result_P <= 60)
		   str = result_P + "점! 오~~ 완존잘하시네";
	   else if(60 < result_P && result_P <= 100)
		   str = result_P + "점?!?! (ㅇㅅ ㅇ)? 버그씀?";
	   else
		   str = result_P + "점은 error!!!!!임!!!";
   }
   	  
   public void exitorrestart() {
	   if(stoporgo != 0){
		   dispose();
		   System.exit(0);
	   }
   }
   	  
   public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("재시작")) {
         dispose();// 이 프레임만 닫기
         new Gameplay();
      }
   }

}