package atualizacoesTelecontrol;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import com.sankhya.util.TimeUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.SPBeanUtils;

public class TelecontrolInsereLiberacoesLimite implements ScheduledAction {

	@Override
	public void onTime(ScheduledActionContext ctx) {
		
		JdbcWrapper jdbc = null;
        NativeSql sql = null;
        ResultSet rset = null;
        JapeSession.SessionHandle hnd = null;
			try {
				hnd = JapeSession.open();
				hnd.setFindersMaxRows(-1);
				EntityFacade entity = EntityFacadeFactory.getDWFFacade();
				jdbc = entity.getJdbcWrapper();
				jdbc.openSession();
	
				sql = new NativeSql(jdbc);
	
				sql.appendSql("SELECT \r\n"
						+ "CAB.NUNOTA AS NUNOTA,\r\n"
						+ "CAB.VLRNOTA AS VLRNOTA,\r\n"
						+ "CAB.CODTIPOPER AS CODTIPOPER,\r\n"
						+ "CAB.CODCENCUS AS CODCENCUS\r\n"
						+ "FROM SANKHYA.TGFCAB CAB\r\n"
						+ "LEFT JOIN SANKHYA.TSILIB LIB ON LIB.NUCHAVE = CAB.NUNOTA\r\n"
						+ "WHERE\r\n"
						+ "CAB.CODTIPOPER = 3131\r\n"
						+ "AND CAB.CODCENCUS = 10700\r\n"
						+ "AND AD_PEDIDO_TELECONTROL IS NOT NULL\r\n"
						+ "AND LIB.NUCHAVE IS NULL");
	
	
				rset = sql.executeQuery();
	
				while (rset.next()) {
				
					BigDecimal nuNota = rset.getBigDecimal("NUNOTA");
					BigDecimal vlrNota = rset.getBigDecimal("VLRNOTA");
					BigDecimal codTipOper = rset.getBigDecimal("CODTIPOPER");
					BigDecimal cenCus = rset.getBigDecimal("CODCENCUS");


					addTelaLiberacao(ctx, nuNota, vlrNota, codTipOper, cenCus);
				}
	
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
	            JapeSession.close(hnd);
	        }
	}
	
	public void addTelaLiberacao(ScheduledActionContext ctx, BigDecimal nuNota,  BigDecimal vlrNota, BigDecimal codTipOper, BigDecimal cenCus ) throws Exception {		
		
		// TODO Auto-generated method stub
				JdbcWrapper jdbc = null;
				NativeSql sql = null;
				ResultSet rset = null;
				SessionHandle hnd = null;
				//JapeSession.SessionHandle hnd = null;
				try {
					hnd = JapeSession.open();
		            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

					JapeWrapper cotDAO = JapeFactory.dao("LiberacaoLimite");
					
					DynamicVO cotVo = cotDAO.create()
							.set("NUCHAVE", nuNota)
							.set("TABELA", "TGFCAB")
							.set("EVENTO", BigDecimal.valueOf(18))
							.set("CODUSUSOLICIT", BigDecimal.ZERO)
							.set("DHSOLICIT", TimeUtils.getNow())
							.set("VLRTOTAL",BigDecimal.ZERO)
							.set("PERCANTERIOR",BigDecimal.ZERO)
							.set("VLRANTERIOR",BigDecimal.ZERO)
							.set("VLRLIMITE",BigDecimal.ZERO)
							.set("VLRATUAL",  BigDecimal.valueOf(1))
							.set("CODUSULIB", BigDecimal.valueOf(4))
							.save();
					
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Erro ao Executar Açao Agendada - liberacao");

				}finally {
		            JapeSession.close(hnd);
		        }
		
		
	}
	

}
