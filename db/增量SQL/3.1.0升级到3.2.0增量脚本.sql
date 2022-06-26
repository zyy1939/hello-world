-- ----------------------------
-- Table structure for sys_role_index
-- ----------------------------
CREATE TABLE `sys_role_index`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由地址',
  `priority` int(11) NULL DEFAULT 0 COMMENT '优先级',
  `status` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '状态0:无效 1:有效',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人登录名称',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人登录名称',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
  `sys_org_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属部门',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色首页表' ROW_FORMAT = Dynamic;

CREATE TABLE `xiao_tao_vip` (
`id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
`create_by` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
`create_time` datetime DEFAULT NULL COMMENT '创建日期',
`update_by` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
`update_time` datetime DEFAULT NULL COMMENT '更新日期',
`sys_org_code` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属部门',
`name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '歌曲名称',
`size` double DEFAULT NULL COMMENT '大小(bit)',
`type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型(wav,flac,mp3)',
`path` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路径',
`download` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '下载地址',
PRIMARY KEY (`id`),
UNIQUE KEY `idx_name_size` (`name`,`size`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='音乐网';