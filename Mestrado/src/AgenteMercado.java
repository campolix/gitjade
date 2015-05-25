


import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentState;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


public class AgenteMercado extends Agent {
	// Constants - public final static
    //////////////////////////////////
	 
	
    // Static variables - public static
    //////////////////////////////////
	private double demanda = 25000;
	
	public AID a;
	
	//public double precoComercializado = 90;
	//public double[] vetorPrecoComercializado = new double[6];
	public double[] vetorPrecoComercializado = {90,86};
	String[] precoComercializado;
    // Instance variables - protected int
    //////////////////////////////////


    // Constructors - public
    //////////////////////////////////


    // External signature methods
    //////////////////////////////////
	
	/** Set up do Agente 
     * - Resumo: 
     */
	private static final long serialVersionUID = 1L;

	public AgenteMercado(){
		
	}
	public AgenteMercado(AID a){
		//this.a = a;
	}
	
	protected void setup(){
		
		// Registrando Agente no DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		sd.setType("Mercado");
		dfd.addServices(sd);
		try{
			DFService.register(this, dfd);
		}
		catch(FIPAException fe){
			fe.printStackTrace();
		}
		
		System.out.println("---->Agente <"+ getAID().getLocalName()+">:\n" +
		"           Iniciado. Serviço Registrado no DF");
		
		Object[] args = getArguments();
		if(args != null && args.length>0){
			precoComercializado = new String[args.length];
			System.out.println("---->Agente Mercado> Mercado apresentas os seguintes Preços do período:");
			for (int i = 0; i < args.length; i++ ){
				precoComercializado[i] = (String)args[i];
				System.out.println("       "+ precoComercializado[i] );
			}
		}
		
		
		
		 

		
		
// #####----------- Comportamentos ------------###############
			buscaServico();
			addBehaviour(new MercadoEnviaDados());
			
		//addBehaviour(new RecebeMensagem());		
		

		
	} //Fim Setup();
	
//	#####----------- Classes ------------###############	
	//Internal Classes
	/*
	public class ProcuraOperadorSistema extends CyclicBehaviour{

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			DFAgentDescription template = new DFAgentDescription();
            ServiceDescription desc  = new ServiceDescription();
            desc.setType( "os" );
            template.addServices(desc);
            
            try{
            	DFAgentDescription[] result = DFService.search(this, template);
            	for (int i = 1; i < result.length; i++){
            		String out = Result[i].getn
            	}//fim for
            }//fim try
            
            
            System.out.println(result.length + " results" );
            if (result.length>0)
                System.out.println(" " + result[0].getName() );
			
		}
		
	}// fim da Classe: ProcuraOperadorSistema
	*/
	
	public class MercadoEnviaDados extends OneShotBehaviour{

		private static final long serialVersionUID = 1L;
		//private double precoComercializado;
		public void action(){
			
			try {
				enviaPrecoComercializado();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
			
		
			
	}
	
	public class RecebeMensagem extends CyclicBehaviour {
		
		int contador = 0;
		Double[] despacho;
		private static final long serialVersionUID = 1L;

		public void action(){
			
			//ACLMessage msg = myAgent.receive();
			ACLMessage msg = blockingReceive();
			if(msg == null){
				block();
				return;
			}
			System.out.println("--AgMer--> Mensagem recebida: "+msg.getContent());

			if (msg != null){
					
				System.out.println("--AgMer--> "+getLocalName()+ " recebeu de " + msg.getSender().getLocalName() +" a msg: " + msg.getContent());				
			}
				
		}
	}
	
	
//	#####----------- Metodos ------------###############
	
		
	private void enviaPrecoComercializado() throws IOException{
		
		
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID("os", AID.ISLOCALNAME));
		msg.setOntology("Mercado");
		//msg.setContent(String.valueOf(precoComercializado));
		// enviar um vetor com valores do periodo de estudo
		msg.setContentObject(precoComercializado);
		send(msg);
		                    
		System.out.println("------------------------------------------------------\n" +
				"---->Agente <"+ getAID().getLocalName()+">:\n" +
				" Cria uma Msg para mandar para AgenteOS \n "+
				"           Envia msg a : OS \n" +
				"           A performativa(Inform = 7) da msg é: "+ msg.getPerformative()+"\n" +
				"           A ontologoia(Mercado) da mensagem é: "+msg.getOntology()+"\n" +
				"           O conteúdo da mensagem é: "+msg.getContent()+"\n"+
				"======================================================");
		//Capturando estado do agente
		System.out.println("==== Estado do Agente Mercado: "+this.getAgentState());	
		
		
		
	}
	
	
	public void buscaServico(){
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		String servico = "operador-do-sistema";
		sd.setName(servico);
		sd.setType(servico);
		template.addServices(sd);
		
		try{
			DFAgentDescription[] result = DFService.search(this, template);			
			AID[] endServico = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
            	endServico[i] = result[i].getName();
            	System.out.println("---->Agente <"+ getAID().getLocalName()+">:\n" +
    					"           buscando AgenteOS\n"
    					+result.length+" AGENTE(S) ESTUDO ENCONTRADO(S) ....\n" +
    							"O nome do Agente é: "+endServico[i].getLocalName());
            }
		}
		catch (FIPAException fe) {
            fe.printStackTrace();
        } 
		
	}
	
	public AgentState getAgentState() {
		// TODO Auto-generated method stub
		return super.getAgentState();
	}
	
	public AID getA() {
		return a;
	}
	public void setA(AID a) {
		this.a = a;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public void setDemanda(double demanda) {
		this.demanda = demanda;
	}
	public double getDemanda() {
		return demanda;
	}
	
	
	
	protected void takeDown(){
		//Desregistrando do DF
		try{
			DFService.deregister(this);
		}
		catch(FIPAException fe){
			fe.printStackTrace();
		}
		
		System.out.println("--AgMercado--> Agente "+getAID().getLocalName()+"terminando.");
	}
}




/*---------- Códigos disponíveis

//Capturando estado do agente
		//System.out.println(this.getAgentState());

private class EnviaStatus extends OneShotBehaviour{

	private static final long serialVersionUID = 1L;

	public void action() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID("ONS", AID.ISLOCALNAME));
		msg.setLanguage("Portugues");
		msg.setOntology("Dados de Mercado");
		msg.setContent("status");
		send(msg);
		
		System.out.println("--AgMer--> Agente <"+getLocalName()+"> enviou Mensagem para ONS \n o Conteúdo da Mensagem é: "+msg.getContent());
	}
}

private class MercadoInformaDadosONS extends OneShotBehaviour{

	private static final long serialVersionUID = 1L;

	public void action(){
		
		
		
		String conteudo = String.valueOf(precoComercializado);
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID("ONS", AID.ISLOCALNAME));
		msg.setLanguage("Portugues");
		msg.setOntology("Dados de Mercado");
		msg.setContent(conteudo);
		send(msg);
		System.out.println("--AgUsi--> O Agente "+msg.getSender().getLocalName()+ " enviou  seu preço para ONS R$"+msg.getContent());
	}
	
}
*/