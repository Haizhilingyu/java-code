

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;

/**
 * modbus通讯工具类,采用modbus4j实现
 *
 * @author lxq
 * @dependencies modbus4j-3.0.3.jar
 * @website https://github.com/infiniteautomation/modbus4j
 */
public class Modbus4jUtils {
    /**
     * 工厂。
     */
    static ModbusFactory modbusFactory;
    static {
        if (modbusFactory == null) {
            modbusFactory = new ModbusFactory();
        }
    }

    /**
     * 获取master
     *
     * @return
     * @throws ModbusInitException
     */
    public static ModbusMaster getMaster() throws ModbusInitException {
        IpParameters params = new IpParameters();
        params.setHost("192.168.0.10");
        params.setPort(502);
        //
        // modbusFactory.createRtuMaster(wapper); //RTU 协议
        // modbusFactory.createUdpMaster(params);//UDP 协议
        // modbusFactory.createAsciiMaster(wrapper);//ASCII 协议
        ModbusMaster master = modbusFactory.createTcpMaster(params, false);
        // TCP 协议
        master.init();

        return master;
    }

    /**
     * 读取[01 Coil Status 0x]类型 开关数据
     *
     * @param slaveId
     *            slaveId
     * @param offset
     *            位置
     * @return 读取值
     * @throws ModbusTransportException
     *             异常
     * @throws ErrorResponseException
     *             异常
     * @throws ModbusInitException
     *             异常
     */
    public static Boolean readCoilStatus(int slaveId, int offset)
            throws ModbusTransportException, ErrorResponseException, ModbusInitException {
        // 01 Coil Status
        BaseLocator<Boolean> loc = BaseLocator.coilStatus(slaveId, offset);
        Boolean value = getMaster().getValue(loc);
        return value;
    }

    /**
     * 读取[02 Input Status 1x]类型 开关数据
     *
     * @param slaveId
     * @param offset
     * @return
     * @throws ModbusTransportException
     * @throws ErrorResponseException
     * @throws ModbusInitException
     */
    public static Boolean readInputStatus(int slaveId, int offset)
            throws ModbusTransportException, ErrorResponseException, ModbusInitException {
        // 02 Input Status
        BaseLocator<Boolean> loc = BaseLocator.inputStatus(slaveId, offset);
        Boolean value = getMaster().getValue(loc);
        return value;
    }

    /**
     * 读取[03 Holding Register类型 2x]模拟量数据
     *
     * @param slaveId
     *            slave Id
     * @param offset
     *            位置
     * @param dataType
     *            数据类型,来自com.serotonin.modbus4j.code.DataType
     * @return
     * @throws ModbusTransportException
     *             异常
     * @throws ErrorResponseException
     *             异常
     * @throws ModbusInitException
     *             异常
     */
    public static Number readHoldingRegister(int slaveId, int offset, int dataType)
            throws ModbusTransportException, ErrorResponseException, ModbusInitException {
        // 03 Holding Register类型数据读取
        BaseLocator<Number> loc = BaseLocator.holdingRegister(slaveId, offset, dataType);
        Number value = getMaster().getValue(loc);
        return value;
    }

    /**
     * 读取[04 Input Registers 3x]类型 模拟量数据
     *
     * @param slaveId
     *            slaveId
     * @param offset
     *            位置
     * @param dataType
     *            数据类型,来自com.serotonin.modbus4j.code.DataType
     * @return 返回结果
     * @throws ModbusTransportException
     *             异常
     * @throws ErrorResponseException
     *             异常
     * @throws ModbusInitException
     *             异常
     */
    public static Number readInputRegisters(int slaveId, int offset, int dataType)
            throws ModbusTransportException, ErrorResponseException, ModbusInitException {
        // 04 Input Registers类型数据读取
        BaseLocator<Number> loc = BaseLocator.inputRegister(slaveId, offset, dataType);
        Number value = getMaster().getValue(loc);
        return value;
    }

    /**
     * 批量读取使用方法
     *
     * @throws ModbusTransportException
     * @throws ErrorResponseException
     * @throws ModbusInitException
     */
    public static void batchRead() throws ModbusTransportException, ErrorResponseException, ModbusInitException {

        BatchRead<Integer> batch = new BatchRead<Integer>();

        batch.addLocator(0, BaseLocator.holdingRegister(1, 1, DataType.FOUR_BYTE_FLOAT));
        batch.addLocator(1, BaseLocator.inputStatus(1, 0));

        ModbusMaster master = getMaster();

        batch.setContiguousRequests(false);
        BatchResults<Integer> results = master.send(batch);
        System.out.println(results.getValue(0));
        System.out.println(results.getValue(1));
    }

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) {
        try {

            Number v3002 = readHoldingRegister(1, 12290, DataType.FOUR_BYTE_FLOAT);
            Number v3004 = readHoldingRegister(1, 12292, DataType.FOUR_BYTE_FLOAT);
            Number v3006 = readHoldingRegister(1, 12294, DataType.FOUR_BYTE_FLOAT);
            Number v3008 = readHoldingRegister(1, 12296, DataType.FOUR_BYTE_FLOAT);
            Number v300a = readHoldingRegister(1, 12298, DataType.FOUR_BYTE_FLOAT);
            Number v300c = readHoldingRegister(1, 12300, DataType.FOUR_BYTE_FLOAT);
            Number v300e = readHoldingRegister(1, 12302, DataType.FOUR_BYTE_FLOAT);
            Number v3010 = readHoldingRegister(1, 12304, DataType.FOUR_BYTE_FLOAT);
            Number v3012 = readHoldingRegister(1, 12306, DataType.FOUR_BYTE_FLOAT);
            Number v3014 = readHoldingRegister(1, 12308, DataType.FOUR_BYTE_FLOAT);

            System.out.println("v3002:" + v3002);
//            System.out.println("v3003:" + v3003);
            System.out.println("v3004:" + v3004);
//            System.out.println("v3005:" + v3005);
            System.out.println("v3006:" + v3006);
//            System.out.println("v3007:" + v3007);
            System.out.println("v3008:" + v3008);
            System.out.println("v300a:" + v300a);
            System.out.println("v300c:" + v300c);
            System.out.println("v300e:" + v300e);
            System.out.println("v3010:" + v3010);
            System.out.println("v3012:" + v3012);
            System.out.println("v3014:" + v3014);
//            System.out.println("v3009:" + v3009);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
