package net.balhar.ds.sequencer;

/**
 * This class is mainly a Encapsulation of the UDP Datagram. It serializes to String contained data.
 * It also creates new instance from serialized data.
 */
public class Operation {
    private OperationType type;
    private Integer sequenceNumber;
    private Double amount;
    private Integer port;
    private Long originationTime;
    private String identifier;

    public Operation(OperationType type, Double amount, Integer sequenceNumber, Integer port, Long originationTime, String identifier) {
        this.type = type;
        this.amount = amount;
        this.sequenceNumber = sequenceNumber;
        this.port = port;
        this.originationTime = originationTime;
        this.identifier = identifier;
    }

    public OperationType getType() {
        return type;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getPort() {
        return port;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Long getOriginationTime() {
        return originationTime;
    }

    public void setOriginationTime(Long originationTime) {
        this.originationTime = originationTime;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String serialize() {
        return String.format("%s;%s;%s;%s;%s;%s;", getType().name(), getAmount(), String.valueOf(getSequenceNumber()),
                String.valueOf(getPort()), String.valueOf(originationTime), getIdentifier());
    }

    public static Operation deserialize(String stringRepresentation){
        String[] operationParts = stringRepresentation.trim().split(";");
        if(operationParts.length < 6){
            throw new RuntimeException("Wrong data from web");
        }
        OperationType type = OperationType.valueOf(operationParts[0]);
        Double money = Double.parseDouble(operationParts[1]);
        Integer sequenceNumber = null;
        if(operationParts[2] != null && !operationParts[2].equals("") && !operationParts[2].equalsIgnoreCase("null")){
            sequenceNumber = Integer.parseInt(operationParts[2]);
        }
        Integer port = Integer.parseInt(operationParts[3]);
        Long originTime = Long.parseLong(operationParts[4]);
        String identifier = operationParts[5];
        return new Operation(type, money, sequenceNumber, port, originTime, identifier);
    }
}
