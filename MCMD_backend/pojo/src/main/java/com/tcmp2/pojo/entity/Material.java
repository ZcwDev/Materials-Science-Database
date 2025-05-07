package com.tcmp2.pojo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

/**
 * 材料实体类
 * 注意：所有数值字段必须是有效的数字，不能包含非数字字符。
 * 如果字段值为空，请使用空字符串""或null，而不是非数字字符。
 *
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 0:06
 */
@Document(collection = "materials")
public class Material {

    @Id
    @Field("_id")
    private String mongoId;

    /** 材料ID */
    @Field("id")
    private String id;

    /** 材料结构 */
    @Field("Structure")
    private String structure;
    
    /** 化学式 */
    @Field("Formula")
    private String formula;
    
    /** 路径 */
    @Field("Path")
    private String path;
    
    /** 晶格参数a (必须是数字) */
    @Field("a")
    private Double a;
    
    /** 晶格参数b (必须是数字) */
    @Field("b")
    private Double b;
    
    /** 晶格参数c (必须是数字) */
    @Field("c")
    private Double c;
    
    /** 晶格参数a1 (必须是数字) */
    @Field("a1")
    private Double a1;
    
    /** 晶格参数b1 (必须是数字) */
    @Field("b1")
    private Double b1;
    
    /** 晶格参数c1 (必须是数字) */
    @Field("c1")
    private Double c1;
    
    /** 表面 */
    @Field("Surface")
    private String surface;
    
    /** 空间群符号 */
    @Field("Space_group_sym")
    private String spaceGroupSym;
    
    /** 空间群编号 (必须是整数) */
    @Field("Space_group_num")
    private Integer spaceGroupNum;
    
    /** 点群 */
    @Field("Point_group")
    private String pointGroup;
    
    /** 晶系 */
    @Field("Crystal_system")
    private String crystalSystem;
    
    /** 形成能 (必须是数字) */
    @Field("Formation_energy")
    private Double formationEnergy;
    
    /** 奈尔温度 */
    @Field("T_N")
    private String tN;  
    
    /** 访问温度 */
    @Field("T_access")
    private String tAccess;  
    
    /** 磁离子 */
    @Field("Mag_ion")
    private String magIon;
    
    /** 单位晶胞体积 */
    @Field("V_unit_cell")
    private String vUnitCell;  
    
    /** 磁离子体积 */
    @Field("Mag_ion_V")
    private String magIonV;  
    
    /** 磁离子质量 */
    @Field("Mag_ion_m")
    private String magIonM;  
    
    /** Nm_Rln_2J_1_ */
    @Field("Nm_Rln_2J_1_")
    private String nmRln2J1;  
    
    /** S_GS_mol */
    @Field("S_GS_mol")
    private String sGSMol;  
    
    /** S_GS_mol2 */
    @Field("S_GS_mol2")
    private String sGSMol2;  
    
    /** S_GS_Vol */
    @Field("S_GS_Vol")
    private String sGSVol;  
    
    /** S_GS_Mass */
    @Field("S_GS_Mass")
    private String sGSMass;  
    
    /** 密度 */
    @Field("Density")
    private String density;  
    
    /** 摩尔质量 */
    @Field("Mole_Mass")
    private String moleMass;  
    
    /** 磁晶格 */
    @Field("magnetic_lattice")
    private String magneticLattice;
    
    /** g因子 */
    @Field("g_factor")
    private String gFactor;  
    
    /** 摩尔体积 */
    @Field("Mole_V")
    private String moleV;  
    
    /** DOI */
    @Field("DOI")
    private String doi;

    /** CuKa XRD数据 */
    @Field("CuKa")
    private String cuKa;
    
    /** CuKa1 XRD数据 */
    @Field("CuKa1")
    private String cuKa1;
    
    /** CuKa2 XRD数据 */
    @Field("CuKa2")
    private String cuKa2;

    private Map<String, Object> properties; // 其他动态字段

    // Getters and Setters
    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Double getA() {
        return a;
    }

    public void setA(Double a) {
        this.a = a;
    }

    public Double getB() {
        return b;
    }

    public void setB(Double b) {
        this.b = b;
    }

    public Double getC() {
        return c;
    }

    public void setC(Double c) {
        this.c = c;
    }

    public Double getA1() {
        return a1;
    }

    public void setA1(Double a1) {
        this.a1 = a1;
    }

    public Double getB1() {
        return b1;
    }

    public void setB1(Double b1) {
        this.b1 = b1;
    }

    public Double getC1() {
        return c1;
    }

    public void setC1(Double c1) {
        this.c1 = c1;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public String getSpaceGroupSym() {
        return spaceGroupSym;
    }

    public void setSpaceGroupSym(String spaceGroupSym) {
        this.spaceGroupSym = spaceGroupSym;
    }

    public Integer getSpaceGroupNum() {
        return spaceGroupNum;
    }

    public void setSpaceGroupNum(Integer spaceGroupNum) {
        this.spaceGroupNum = spaceGroupNum;
    }

    public String getPointGroup() {
        return pointGroup;
    }

    public void setPointGroup(String pointGroup) {
        this.pointGroup = pointGroup;
    }

    public String getCrystalSystem() {
        return crystalSystem;
    }

    public void setCrystalSystem(String crystalSystem) {
        this.crystalSystem = crystalSystem;
    }

    public Double getFormationEnergy() {
        return formationEnergy;
    }

    public void setFormationEnergy(Double formationEnergy) {
        this.formationEnergy = formationEnergy;
    }

    public String gettN() {
        return tN;
    }

    public void settN(String tN) {
        this.tN = tN;
    }

    public String gettAccess() {
        return tAccess;
    }

    public void settAccess(String tAccess) {
        this.tAccess = tAccess;
    }

    public String getMagIon() {
        return magIon;
    }

    public void setMagIon(String magIon) {
        this.magIon = magIon;
    }

    public String getvUnitCell() {
        return vUnitCell;
    }

    public void setvUnitCell(String vUnitCell) {
        this.vUnitCell = vUnitCell;
    }

    public String getMagIonV() {
        return magIonV;
    }

    public void setMagIonV(String magIonV) {
        this.magIonV = magIonV;
    }

    public String getMagIonM() {
        return magIonM;
    }

    public void setMagIonM(String magIonM) {
        this.magIonM = magIonM;
    }

    public String getNmRln2J1() {
        return nmRln2J1;
    }

    public void setNmRln2J1(String nmRln2J1) {
        this.nmRln2J1 = nmRln2J1;
    }

    public String getsGSMol() {
        return sGSMol;
    }

    public void setsGSMol(String sGSMol) {
        this.sGSMol = sGSMol;
    }

    public String getsGSMol2() {
        return sGSMol2;
    }

    public void setsGSMol2(String sGSMol2) {
        this.sGSMol2 = sGSMol2;
    }

    public String getsGSVol() {
        return sGSVol;
    }

    public void setsGSVol(String sGSVol) {
        this.sGSVol = sGSVol;
    }

    public String getsGSMass() {
        return sGSMass;
    }

    public void setsGSMass(String sGSMass) {
        this.sGSMass = sGSMass;
    }

    public String getDensity() {
        return density;
    }

    public void setDensity(String density) {
        this.density = density;
    }

    public String getMoleMass() {
        return moleMass;
    }

    public void setMoleMass(String moleMass) {
        this.moleMass = moleMass;
    }

    public String getMagneticLattice() {
        return magneticLattice;
    }

    public void setMagneticLattice(String magneticLattice) {
        this.magneticLattice = magneticLattice;
    }

    public String getgFactor() {
        return gFactor;
    }

    public void setgFactor(String gFactor) {
        this.gFactor = gFactor;
    }

    public String getMoleV() {
        return moleV;
    }

    public void setMoleV(String moleV) {
        this.moleV = moleV;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getCuKa() {
        return cuKa;
    }

    public void setCuKa(String cuKa) {
        this.cuKa = cuKa;
    }

    public String getCuKa1() {
        return cuKa1;
    }

    public void setCuKa1(String cuKa1) {
        this.cuKa1 = cuKa1;
    }

    public String getCuKa2() {
        return cuKa2;
    }

    public void setCuKa2(String cuKa2) {
        this.cuKa2 = cuKa2;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
