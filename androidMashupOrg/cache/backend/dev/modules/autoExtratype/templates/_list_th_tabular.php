  <th id="sf_admin_list_th_id_extratype">
          <?php if ($sf_user->getAttribute('sort', null, 'sf_admin/extratype/sort') == 'id_extratype'): ?>
      <?php echo link_to(__('Id extratype'), 'extratype/list?sort=id_extratype&type='.($sf_user->getAttribute('type', 'asc', 'sf_admin/extratype/sort') == 'asc' ? 'desc' : 'asc')) ?>
      (<?php echo __($sf_user->getAttribute('type', 'asc', 'sf_admin/extratype/sort')) ?>)
      <?php else: ?>
      <?php echo link_to(__('Id extratype'), 'extratype/list?sort=id_extratype&type=asc') ?>
      <?php endif; ?>
          </th>
  <th id="sf_admin_list_th_name">
          <?php if ($sf_user->getAttribute('sort', null, 'sf_admin/extratype/sort') == 'name'): ?>
      <?php echo link_to(__('Name'), 'extratype/list?sort=name&type='.($sf_user->getAttribute('type', 'asc', 'sf_admin/extratype/sort') == 'asc' ? 'desc' : 'asc')) ?>
      (<?php echo __($sf_user->getAttribute('type', 'asc', 'sf_admin/extratype/sort')) ?>)
      <?php else: ?>
      <?php echo link_to(__('Name'), 'extratype/list?sort=name&type=asc') ?>
      <?php endif; ?>
          </th>
  <th id="sf_admin_list_th_description">
          <?php if ($sf_user->getAttribute('sort', null, 'sf_admin/extratype/sort') == 'description'): ?>
      <?php echo link_to(__('Description'), 'extratype/list?sort=description&type='.($sf_user->getAttribute('type', 'asc', 'sf_admin/extratype/sort') == 'asc' ? 'desc' : 'asc')) ?>
      (<?php echo __($sf_user->getAttribute('type', 'asc', 'sf_admin/extratype/sort')) ?>)
      <?php else: ?>
      <?php echo link_to(__('Description'), 'extratype/list?sort=description&type=asc') ?>
      <?php endif; ?>
          </th>
  <th id="sf_admin_list_th_java_retrieve_command">
          <?php if ($sf_user->getAttribute('sort', null, 'sf_admin/extratype/sort') == 'java_retrieve_command'): ?>
      <?php echo link_to(__('Java retrieve command'), 'extratype/list?sort=java_retrieve_command&type='.($sf_user->getAttribute('type', 'asc', 'sf_admin/extratype/sort') == 'asc' ? 'desc' : 'asc')) ?>
      (<?php echo __($sf_user->getAttribute('type', 'asc', 'sf_admin/extratype/sort')) ?>)
      <?php else: ?>
      <?php echo link_to(__('Java retrieve command'), 'extratype/list?sort=java_retrieve_command&type=asc') ?>
      <?php endif; ?>
          </th>
