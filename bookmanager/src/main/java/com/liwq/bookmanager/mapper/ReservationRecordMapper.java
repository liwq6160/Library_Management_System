package com.liwq.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.dto.ReservationDetailDTO;
import com.liwq.bookmanager.model.ReservationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 预约记录Mapper接口
 */
@Mapper
public interface ReservationRecordMapper extends BaseMapper<ReservationRecord> {

    /**
     * 查询预约详情（包含用户和图书信息）
     */
    @Select("SELECT r.id, r.user_id, u.username, u.real_name, r.book_id, b.book_name, b.author, " +
            "b.cover_image, r.reservation_date, r.status, r.remark, r.create_time, r.update_time " +
            "FROM reservation_records r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "LEFT JOIN books b ON r.book_id = b.id " +
            "WHERE r.id = #{id}")
    ReservationDetailDTO selectReservationDetailById(@Param("id") Long id);

    /**
     * 分页查询我的预约记录（包含图书信息）
     */
    @Select("<script>" +
            "SELECT r.id, r.user_id, u.username, u.real_name, r.book_id, b.book_name, b.author, " +
            "b.cover_image, r.reservation_date, r.status, r.remark, r.create_time, r.update_time " +
            "FROM reservation_records r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "LEFT JOIN books b ON r.book_id = b.id " +
            "WHERE r.user_id = #{userId} " +
            "<if test='status != null and status != \"\"'>" +
            "AND r.status = #{status} " +
            "</if>" +
            "ORDER BY r.create_time DESC" +
            "</script>")
    Page<ReservationDetailDTO> selectMyReservationPage(Page<ReservationDetailDTO> page,
                                                       @Param("userId") Long userId,
                                                       @Param("status") String status);

    /**
     * 分页查询所有预约记录（管理员，包含用户和图书信息）
     */
    @Select("<script>" +
            "SELECT r.id, r.user_id, u.username, u.real_name, r.book_id, b.book_name, b.author, " +
            "b.cover_image, r.reservation_date, r.status, r.remark, r.create_time, r.update_time " +
            "FROM reservation_records r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "LEFT JOIN books b ON r.book_id = b.id " +
            "WHERE 1=1 " +
            "<if test='userId != null'>" +
            "AND r.user_id = #{userId} " +
            "</if>" +
            "<if test='bookId != null'>" +
            "AND r.book_id = #{bookId} " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "AND r.status = #{status} " +
            "</if>" +
            "ORDER BY r.create_time DESC" +
            "</script>")
    Page<ReservationDetailDTO> selectAllReservationPage(Page<ReservationDetailDTO> page,
                                                        @Param("userId") Long userId,
                                                        @Param("bookId") Long bookId,
                                                        @Param("status") String status);
}
