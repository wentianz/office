package cn.wen.office.mapper;

import cn.wen.office.model.CheckInOutRecord;
import cn.wen.office.model.CheckInOutRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CheckInOutRecordMapper {
    long countByExample(CheckInOutRecordExample example);

    int deleteByExample(CheckInOutRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CheckInOutRecord record);

    int insertSelective(CheckInOutRecord record);

    List<CheckInOutRecord> selectByExample(CheckInOutRecordExample example);

    CheckInOutRecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") CheckInOutRecord record, @Param("example") CheckInOutRecordExample example);

    int updateByExample(@Param("record") CheckInOutRecord record, @Param("example") CheckInOutRecordExample example);

    int updateByPrimaryKeySelective(CheckInOutRecord record);

    int updateByPrimaryKey(CheckInOutRecord record);
}