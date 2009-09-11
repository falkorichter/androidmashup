  <th id="sf_admin_list_th_id_intent_handles_extra">
          <?php if ($sf_user->getAttribute('sort', null, 'sf_admin/intent_handles_extra/sort') == 'id_intent_handles_extra'): ?>
      <?php echo link_to(__('Id intent handles extra'), 'IntentHandlesExtra/list?sort=id_intent_handles_extra&type='.($sf_user->getAttribute('type', 'asc', 'sf_admin/intent_handles_extra/sort') == 'asc' ? 'desc' : 'asc')) ?>
      (<?php echo __($sf_user->getAttribute('type', 'asc', 'sf_admin/intent_handles_extra/sort')) ?>)
      <?php else: ?>
      <?php echo link_to(__('Id intent handles extra'), 'IntentHandlesExtra/list?sort=id_intent_handles_extra&type=asc') ?>
      <?php endif; ?>
          </th>
  <th id="sf_admin_list_th_intent_id">
          <?php if ($sf_user->getAttribute('sort', null, 'sf_admin/intent_handles_extra/sort') == 'intent_id'): ?>
      <?php echo link_to(__('Intent'), 'IntentHandlesExtra/list?sort=intent_id&type='.($sf_user->getAttribute('type', 'asc', 'sf_admin/intent_handles_extra/sort') == 'asc' ? 'desc' : 'asc')) ?>
      (<?php echo __($sf_user->getAttribute('type', 'asc', 'sf_admin/intent_handles_extra/sort')) ?>)
      <?php else: ?>
      <?php echo link_to(__('Intent'), 'IntentHandlesExtra/list?sort=intent_id&type=asc') ?>
      <?php endif; ?>
          </th>
  <th id="sf_admin_list_th_extra_id">
          <?php if ($sf_user->getAttribute('sort', null, 'sf_admin/intent_handles_extra/sort') == 'extra_id'): ?>
      <?php echo link_to(__('Extra'), 'IntentHandlesExtra/list?sort=extra_id&type='.($sf_user->getAttribute('type', 'asc', 'sf_admin/intent_handles_extra/sort') == 'asc' ? 'desc' : 'asc')) ?>
      (<?php echo __($sf_user->getAttribute('type', 'asc', 'sf_admin/intent_handles_extra/sort')) ?>)
      <?php else: ?>
      <?php echo link_to(__('Extra'), 'IntentHandlesExtra/list?sort=extra_id&type=asc') ?>
      <?php endif; ?>
          </th>
  <th id="sf_admin_list_th_mandatory">
          <?php if ($sf_user->getAttribute('sort', null, 'sf_admin/intent_handles_extra/sort') == 'mandatory'): ?>
      <?php echo link_to(__('Mandatory'), 'IntentHandlesExtra/list?sort=mandatory&type='.($sf_user->getAttribute('type', 'asc', 'sf_admin/intent_handles_extra/sort') == 'asc' ? 'desc' : 'asc')) ?>
      (<?php echo __($sf_user->getAttribute('type', 'asc', 'sf_admin/intent_handles_extra/sort')) ?>)
      <?php else: ?>
      <?php echo link_to(__('Mandatory'), 'IntentHandlesExtra/list?sort=mandatory&type=asc') ?>
      <?php endif; ?>
          </th>
  <th id="sf_admin_list_th_description">
          <?php if ($sf_user->getAttribute('sort', null, 'sf_admin/intent_handles_extra/sort') == 'description'): ?>
      <?php echo link_to(__('Description'), 'IntentHandlesExtra/list?sort=description&type='.($sf_user->getAttribute('type', 'asc', 'sf_admin/intent_handles_extra/sort') == 'asc' ? 'desc' : 'asc')) ?>
      (<?php echo __($sf_user->getAttribute('type', 'asc', 'sf_admin/intent_handles_extra/sort')) ?>)
      <?php else: ?>
      <?php echo link_to(__('Description'), 'IntentHandlesExtra/list?sort=description&type=asc') ?>
      <?php endif; ?>
          </th>
