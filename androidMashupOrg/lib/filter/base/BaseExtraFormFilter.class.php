<?php

require_once(sfConfig::get('sf_lib_dir').'/filter/base/BaseFormFilterPropel.class.php');

/**
 * Extra filter form base class.
 *
 * @package    mashup
 * @subpackage filter
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormFilterGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseExtraFormFilter extends BaseFormFilterPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'name'         => new sfWidgetFormFilterInput(),
      'description'  => new sfWidgetFormFilterInput(),
      'extraType_id' => new sfWidgetFormPropelChoice(array('model' => 'Extratype', 'add_empty' => true)),
    ));

    $this->setValidators(array(
      'name'         => new sfValidatorPass(array('required' => false)),
      'description'  => new sfValidatorPass(array('required' => false)),
      'extraType_id' => new sfValidatorPropelChoice(array('required' => false, 'model' => 'Extratype', 'column' => 'id_extraType')),
    ));

    $this->widgetSchema->setNameFormat('extra_filters[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'Extra';
  }

  public function getFields()
  {
    return array(
      'id_extra'     => 'Number',
      'name'         => 'Text',
      'description'  => 'Text',
      'extraType_id' => 'ForeignKey',
    );
  }
}
