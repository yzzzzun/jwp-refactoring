package kitchenpos.table.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.order.domain.Order;
import kitchenpos.order.repository.OrderRepository;
import kitchenpos.table.domain.OrderTable;
import kitchenpos.table.domain.TableGroup;
import kitchenpos.table.dto.TableGroupRequest;
import kitchenpos.table.dto.TableGroupResponse;
import kitchenpos.table.repository.OrderTableRepository;
import kitchenpos.table.repository.TableGroupRepository;

@Service
@Transactional(readOnly = true)
public class TableGroupService {
	private final OrderRepository orderRepository;
	private final OrderTableRepository orderTableRepository;
	private final TableGroupRepository tableGroupRepository;

	public TableGroupService(OrderRepository orderRepository,
		OrderTableRepository orderTableRepository, TableGroupRepository tableGroupRepository) {
		this.orderRepository = orderRepository;
		this.orderTableRepository = orderTableRepository;
		this.tableGroupRepository = tableGroupRepository;
	}

	@Transactional
	public TableGroupResponse create(final TableGroupRequest tableGroupRequest) {
		List<Long> orderTableIds = tableGroupRequest.getOrderTableIds();
		List<OrderTable> orderTables = orderTableIds.stream().map(this::findOrderTable).collect(Collectors.toList());
		TableGroup tableGroup = TableGroup.of(orderTables);
		return TableGroupResponse.of(tableGroupRepository.save(tableGroup));
	}

	private OrderTable findOrderTable(Long id) {
		return orderTableRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("id에 해당하는 주문 테이블을 찾을 수 없습니다."));
	}

	@Transactional
	public void ungroup(final Long tableGroupId) {
		TableGroup tableGroup = tableGroupRepository.findById(tableGroupId)
			.orElseThrow(() -> new IllegalArgumentException("id에 해당하는 단체지정을 찾을 수 없습니다."));
		List<OrderTable> orderTables = tableGroup.getOrderTables();
		List<Long> orderTableIds = orderTables.stream().map(OrderTable::getId).collect(Collectors.toList());
		List<Order> orders = orderRepository.findByOrderTableIdIn(orderTableIds);
		boolean unChangeable = orders.stream().anyMatch(Order::isUnChangeable);
		if (unChangeable) {
			throw new IllegalArgumentException("주문 상태가 완료되어야 단체지정이 해제가능합니다.");
		}
		tableGroup.unGroup();
	}
}
