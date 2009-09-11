<?php echo form_tag('extratype/save', array(
  'id'        => 'sf_admin_edit_form',
  'name'      => 'sf_admin_edit_form',
  'multipart' => true,
)) ?>

<?php echo object_input_hidden_tag($extratype, 'getIdExtratype') ?>

<fieldset id="sf_fieldset_none" class="">

<div class="form-row">
  <?php echo label_for('extratype[name]', __($labels['extratype{name}']), '') ?>
  <div class="content<?php if ($sf_request->hasError('extratype{name}')): ?> form-error<?php endif; ?>">
  <?php if ($sf_request->hasError('extratype{name}')): ?>
    <?php echo form_error('extratype{name}', array('class' => 'form-error-msg')) ?>
  <?php endif; ?>

  <?php $value = object_input_tag($extratype, 'getName', array (
  'size' => 45,
  'control_name' => 'extratype[name]',
)); echo $value ? $value : '&nbsp;' ?>
    </div>
</div>

<div class="form-row">
  <?php echo label_for('extratype[description]', __($labels['extratype{description}']), '') ?>
  <div class="content<?php if ($sf_request->hasError('extratype{description}')): ?> form-error<?php endif; ?>">
  <?php if ($sf_request->hasError('extratype{description}')): ?>
    <?php echo form_error('extratype{description}', array('class' => 'form-error-msg')) ?>
  <?php endif; ?>

  <?php $value = object_input_tag($extratype, 'getDescription', array (
  'size' => 45,
  'control_name' => 'extratype[description]',
)); echo $value ? $value : '&nbsp;' ?>
    </div>
</div>

<div class="form-row">
  <?php echo label_for('extratype[java_retrieve_command]', __($labels['extratype{java_retrieve_command}']), '') ?>
  <div class="content<?php if ($sf_request->hasError('extratype{java_retrieve_command}')): ?> form-error<?php endif; ?>">
  <?php if ($sf_request->hasError('extratype{java_retrieve_command}')): ?>
    <?php echo form_error('extratype{java_retrieve_command}', array('class' => 'form-error-msg')) ?>
  <?php endif; ?>

  <?php $value = object_input_tag($extratype, 'getJavaRetrieveCommand', array (
  'size' => 45,
  'control_name' => 'extratype[java_retrieve_command]',
)); echo $value ? $value : '&nbsp;' ?>
    </div>
</div>

</fieldset>

<?php include_partial('edit_actions', array('extratype' => $extratype)) ?>

</form>

<ul class="sf_admin_actions">
      <li class="float-left"><?php if ($extratype->getIdExtratype()): ?>
<?php echo button_to(__('delete'), 'extratype/delete?id_extratype='.$extratype->getIdExtratype(), array (
  'post' => true,
  'confirm' => __('Are you sure?'),
  'class' => 'sf_admin_action_delete',
)) ?><?php endif; ?>
</li>
  </ul>
